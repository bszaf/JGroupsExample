package pl.edu.agh.dsrg.sr.chat.chat;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Address;
import org.jgroups.ReceiverAdapter;
import org.jgroups.Message;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.*;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bartoszszafran on 28/03/2017.
 */
public class Receiver extends ReceiverAdapter {

    private JTextArea myArea;


    public Receiver() {}

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        try {
            ChatMessage message = ChatMessage.parseFrom(msg.getBuffer());
            String stringMsg = message.getMessage();
            System.out.println(msg.getSrc() + ": " + stringMsg);
            if (myArea != null) {
                myArea.append(stringMsg + "\n");
            }

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void getState(OutputStream output) throws Exception {

    }

    public void setState(InputStream input) throws Exception {
    }

    public void setMyArea(JTextArea myArea) {
        this.myArea = myArea;
    }
}
