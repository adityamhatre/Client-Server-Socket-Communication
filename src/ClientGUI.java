import javax.swing.*;
import java.awt.*;

/**
 * Created by Aditya Mhatre on 12 Jun, 2017.
 * MavID: 1001429814
 */


/**
 * POJO class that contains GUI elements of the client window and their corresponding get methods
 */
public class ClientGUI {
    private JTextField inputText;
    private JButton sendButton;
    private JPanel rootPanel;
    private JTextField outputText;
    private JButton connectButton;
    private JLabel statusLabel;

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public JTextField getInputText() {
        return inputText;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JTextField getOutputText() {
        return outputText;
    }

}
