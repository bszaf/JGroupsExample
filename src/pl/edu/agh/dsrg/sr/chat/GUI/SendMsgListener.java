package pl.edu.agh.dsrg.sr.chat.GUI;

import pl.edu.agh.dsrg.sr.chat.chat.SimpleChat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by bartoszszafran on 28/03/2017.
 */
public class SendMsgListener implements ActionListener {
    JTextField myInputText;
    JTextArea myOutputText;
    SimpleChat chat;
    String nick;


    public SendMsgListener(JTextField myInputText, JTextArea myOutputText, SimpleChat chat, String nick) {
       this.myInputText = myInputText;
       this.myOutputText = myOutputText;
       this.chat = chat;
       this.nick = nick;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            chat.sendMessage("", nick + ": " + myInputText.getText());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        myInputText.setText("");
    }
}
