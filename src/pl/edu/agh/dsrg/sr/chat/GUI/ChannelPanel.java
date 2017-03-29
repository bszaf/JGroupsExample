package pl.edu.agh.dsrg.sr.chat.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bartoszszafran on 29/03/2017.
 */
public class ChannelPanel extends JPanel implements ActionListener, MouseListener {
    JList<String> channelsList;
    JList<String> usersList;
    private DefaultListModel<String> channelsModel = new DefaultListModel<>();
    private Map<String, DefaultListModel<String>> usersInChannel;
    Gui gui;

    public ChannelPanel(Gui gui) {
        this.gui = gui;
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        channelsList = new JList<>(channelsModel);
        usersList = new JList<>();
        usersInChannel = new HashMap<>();

        channelsList.addMouseListener(this);
        JButton joinButton = new JButton("Join");
        joinButton.addActionListener(this);

        add(channelsList);
        add(usersList);
        add(joinButton);
    }

    public void addChannel(String channelName) {
        channelsModel.addElement(channelName);
        if (!usersInChannel.containsKey(channelName)) {
            usersInChannel.put(channelName, new DefaultListModel<String>());
        }
    }

    public void removeChannel(String channelName) {
        channelsModel.removeElement(channelName);
        if (usersInChannel.containsKey(channelName)) {
            usersInChannel.remove(channelName);
        }
    }

    public void addUser(String channelName, String username) {
        DefaultListModel<String> model =  usersInChannel.get(channelName);
        model.addElement(username);
    }

    public void removeUser(String channelName, String username) {
        DefaultListModel<String> model =  usersInChannel.get(channelName);
        model.removeElement(username);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = channelsList.getSelectedValue();
        channelsList.clearSelection();
        if (name != null && !name.isEmpty())
            gui.joinRoom(name);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String channelName = channelsList.getSelectedValue();
        DefaultListModel<String> model =  usersInChannel.get(channelName);
        usersList.setModel(model);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
