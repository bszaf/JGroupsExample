package pl.edu.agh.dsrg.sr.chat.GUI;/**
 * Created by bartoszszafran on 28/03/2017.
 */

import javafx.application.Application;
import javafx.stage.Stage;
import pl.edu.agh.dsrg.sr.chat.chat.Receiver;
import pl.edu.agh.dsrg.sr.chat.chat.SimpleChat;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    private JTextArea textArea;
    private JTextField textField;
    private SimpleChat chat;
    private String nick;

    public ChatPanel(String roomName, String nick) throws Exception {
        this.chat = new SimpleChat(roomName, roomName);
        this.nick = nick;
        JPanel panel = initTextFields();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }

    private JPanel initTextFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        textArea = new JTextArea();
        textArea.setEditable(false);
        Receiver receiver = (Receiver)chat.getReceiver();
        receiver.setMyArea(textArea);

        textField = new JTextField();

        SendMsgListener sendMsgListener = new SendMsgListener(textField, textArea, chat, nick);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.addActionListener(sendMsgListener);

        panel.add(textArea);
        panel.add(textField);
        return panel;
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
