package pl.edu.agh.dsrg.sr.chat.main;

import pl.edu.agh.dsrg.sr.chat.GUI.ChatPanel;
import pl.edu.agh.dsrg.sr.chat.GUI.Gui;

/**
 * Created by bartoszszafran on 27/03/2017.
 */
public class Main {
    public static void main(String [] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack","true");
        new Gui();
    }
}
