package pl.edu.agh.dsrg.sr.chat.GUI;

import pl.edu.agh.dsrg.sr.chat.chat.SimpleChat;
import pl.edu.agh.dsrg.sr.chat.chat.StateReceiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bartoszszafran on 29/03/2017.
 */
public class Gui extends JFrame {

    JTabbedPane pane;
    JTextField roomName;
    String nick;
    Map<String, ChatPanel> joinedRooms;
    SimpleChat chat;
    ChannelPanel channelPanel;
    JLabel info;


    public Gui() throws Exception {
        this.pane = new JTabbedPane();
        this.joinedRooms = new HashMap<>();
        this.channelPanel = new ChannelPanel(this);
        this.info = new JLabel();

        add(initJoin(), BorderLayout.PAGE_START);
        add(channelPanel, BorderLayout.EAST);
        add(pane, BorderLayout.CENTER);
        add(info, BorderLayout.PAGE_END);

        pack();
        setSize(500,600);
        setVisible(true);
        info.setText("Prompting for nickname");
        nick = promptForNick();
        info.setText("Connecting");
        this.chat = SimpleChat.getManager(channelPanel, this, nick);
        this.addWindowListener();
        info.setText("Connected");

    }

    public void joinRoom(String roomname) {
        if (joinedRooms.containsKey(roomname)) {
            info.setText("Already in room " + roomname);
        } else
        try {
            ChatPanel chatPanel = new ChatPanel(roomname, nick);
            pane.add(chatPanel, roomname);
            chat.joinRoom(roomname, nick);
            joinedRooms.put(roomname, chatPanel);
        } catch (Exception e1) {
            if (e1 instanceof UnknownHostException)
                info.setText("Room name have to be valid multicast address");
            else
                e1.printStackTrace();
        }
    }

    private JPanel initJoin() {
        JPanel panel = new JPanel();

        JButton button = new JButton("Join Channel");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String label = roomName.getText();
                if (label == null || label.isEmpty() )
                    return;
                joinRoom(label);
            }
        });

        roomName = new JTextField();
        roomName.setAutoscrolls(true);
        roomName.setPreferredSize(new Dimension(200, 20));

        JPanel wrapper = new JPanel( new FlowLayout(0, 0, FlowLayout.LEADING) );
        wrapper.add(roomName);

        panel.add(button);
        panel.add(wrapper);
        return panel;

    }

    private String promptForNick() {
        String nick = JOptionPane.showInputDialog(this, "Enter nick", "Nick", JOptionPane.QUESTION_MESSAGE);
        if (nick == null || nick == "")
            return promptForNick();
        else
            return nick;
    }

    public void putInfoOnChannel(String channel, String Info) {
        ChatPanel chatPanel = joinedRooms.get(channel);
        chatPanel.getTextArea().append("*** " + Info + "\n");

    }

    private void addWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.out.print("Leaving");
                chat.disconnect();
                try {
                    Thread.sleep(5000);
                    System.exit(0);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
