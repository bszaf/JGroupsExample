package pl.edu.agh.dsrg.sr.chat.chat;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.demos.Chat;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.GUI.ChannelPanel;
import pl.edu.agh.dsrg.sr.chat.GUI.Gui;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.*;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction.*;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by bartoszszafran on 28/03/2017.
 */
public class SimpleChat {
    private JChannel channel;
    private ReceiverAdapter receiver;
    private String chatCluster;
    private static String MANAGMENT_CHANNEL_NAME = "ChatManagement321321";

    public SimpleChat(String hostname, String chatCluster) throws Exception {
        this.chatCluster = chatCluster;
        this.channel = new JChannel(false);
        this.receiver = new Receiver();
        ProtocolStack stack = getStack(hostname);
        channel.setProtocolStack(stack);
        stack.init();

        channel.setReceiver(receiver);
        this.connect();
    }
    private SimpleChat() {

    }

    public ReceiverAdapter getReceiver() { return receiver;}

    private void connect() {
        try {
            this.channel.connect(chatCluster);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void disconnect() {
        channel.disconnect();
    }

    public void leave(String roomName, String nick) throws Exception {
         ChatAction action = ChatAction.newBuilder()
                .setAction(ActionType.LEAVE)
                .setChannel(roomName)
                .setNickname(nick)
                .build();
        channel.send(new Message(null, null, action.toByteArray()));
        channel.close();
    }

    public void sendMessage(String channelName, String message) throws Exception {
        ChatMessage msg = ChatMessage.newBuilder()
                .setMessage(message)
                .build();
        channel.send(new Message(null, null, msg.toByteArray()));
    }

    public void joinRoom(String roomName, String nick) throws Exception {
        ChatAction action = ChatAction.newBuilder()
                .setAction(ChatAction.ActionType.JOIN)
                .setChannel(roomName)
                .setNickname(nick)
                .build();
        channel.send(new Message(null, null, action.toByteArray()));

    }

    static private ProtocolStack getStack(String Hostname) throws UnknownHostException {
        ProtocolStack stack = new ProtocolStack();
        UDP udp = new UDP();
        if (Hostname != null )
            udp.setValue("mcast_group_addr", InetAddress.getByName(Hostname));
        stack.addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH());
        return stack;
    }

    public static SimpleChat getManager(ChannelPanel channelPanel, Gui gui, String name) throws Exception {
        SimpleChat mychat = new SimpleChat();
        mychat.channel = new JChannel(false);
        mychat.channel.setName(name);
        mychat.chatCluster = SimpleChat.MANAGMENT_CHANNEL_NAME;
        mychat.receiver = new StateReceiver(channelPanel, gui, mychat.channel);
        ProtocolStack stack = getStack(null);
        mychat.channel.setProtocolStack(stack);
        stack.init();

        mychat.channel.setReceiver(mychat.receiver);
        mychat.connect();
        mychat.channel.getState(null, 10000);

        return mychat;
    }
}
