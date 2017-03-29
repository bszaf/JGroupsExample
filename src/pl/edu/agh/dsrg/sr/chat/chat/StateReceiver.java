package pl.edu.agh.dsrg.sr.chat.chat;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.edu.agh.dsrg.sr.chat.GUI.ChannelPanel;
import pl.edu.agh.dsrg.sr.chat.GUI.Gui;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.*;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction.*;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bartoszszafran on 29/03/2017.
 */
public class StateReceiver extends ReceiverAdapter {
    private Map<String, List<String>> channelUsers;
    private ChannelPanel channelPanel;
    private Gui gui;
    private JChannel channel;

    public StateReceiver(ChannelPanel channelPanel, Gui gui, JChannel channel) {
        this.channelUsers = new HashMap<>();
        this.channelPanel = channelPanel;
        this.gui = gui;
        this.channel = channel;
    }

    public void receive(Message msg) {
        System.out.println("[State] ** receive: ");
        ChatAction action;
        try {
            action = ChatAction.parseFrom(msg.getBuffer());

            ActionType actionType = action.getAction();
            String channelName = action.getChannel();
            String nick = action.getNickname();

            synchronized (channelUsers) {
                if (actionType == ActionType.JOIN) {
                    if (!channelUsers.containsKey(channelName)) {
                        channelUsers.put(channelName, new LinkedList<String>());
                        channelPanel.addChannel(channelName);
                    }

                    channelUsers.get(channelName).add(nick);
                    gui.putInfoOnChannel(channelName, "User " + nick + " joined the channel");
                    channelPanel.addUser(channelName, nick);
                } else if (actionType == ActionType.LEAVE) {
                    channelUsers.get(channelName).remove(nick);
                    channelPanel.removeUser(channelName, nick);
                    gui.putInfoOnChannel(channelName, "User " + nick + " leaved the channel");

                    if (channelUsers.get(channelName).isEmpty()) {
                        channelUsers.remove(channelName);
                        channelPanel.removeChannel(channelName);
                    }
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void getState(OutputStream output) throws Exception {
        System.out.println("[State] ** getState: "+ channelUsers);
        synchronized (channelUsers) {
            ChatState.Builder builder = ChatState.newBuilder();

            for (Map.Entry<String, List<String>> entry : channelUsers.entrySet()) {
                String channelName = entry.getKey();
                List<String> users = entry.getValue();

                for (String user : users) {
                    builder.addStateBuilder()
                            .setAction(ActionType.JOIN)
                            .setChannel(channelName)
                            .setNickname(user);
                }
            }

            ChatState state = builder.build();

            state.writeTo(output);
            System.out.println("[State] ** getState: "+ channelUsers);
        }
    }

    public void setState(InputStream input) throws Exception {
        System.out.println("[State] ** setstate: ");
        synchronized (channelUsers) {
            ChatState state = ChatState.parseFrom(input);
            System.out.println("[State] ** setstate: " + state);
            channelUsers.clear();

            for (ChatAction chatAction : state.getStateList()) {
                String channelName = chatAction.getChannel();
                String nick = chatAction.getNickname();

                if (!channelUsers.containsKey(channelName)) {
                    channelUsers.put(channelName, new LinkedList<String>());
                    channelPanel.addChannel(channelName);
                }

                channelUsers.get(channelName).add(nick);
                channelPanel.addUser(channelName, nick);
            }
        }
    }

    public void viewAccepted(View view) {
        System.out.println("[State] ** view: " + view.getMembers());
        System.out.println("[State] ** channelUsers: " + channelUsers);
        synchronized (channelUsers) {

            List<String> users = new LinkedList<>();
            for(Address addr : view.getMembers()) {
                users.add(channel.getName(addr));
            }
            System.out.println("[State] ** member users: " + users);
            for (Map.Entry<String, List<String>> entry : channelUsers.entrySet()) {
                String channelName = entry.getKey();
                List<String> lostUsers = new LinkedList(entry.getValue());

                System.out.println("[State] ** lost users " + lostUsers);
                lostUsers.removeAll(users);
                System.out.println("[State] ** lost users " + lostUsers);

                for (String lostUser : lostUsers) {
                    gui.putInfoOnChannel(channelName, "User " + lostUser + " leaved the channel");
                    channelPanel.removeUser(channelName, lostUser);
                }
                entry.getValue().retainAll(users);
                if (entry.getValue().isEmpty()) {
                    channelPanel.removeChannel(channelName);
                    channelUsers.remove(entry);
                }
            }
        }
        System.out.println("[State] ** channelUsers: " + channelUsers);
    }
}
