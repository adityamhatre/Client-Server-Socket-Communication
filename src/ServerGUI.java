import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Aditya Mhatre on 12 Jun, 2017.
 * MavID: 1001429814
 */

/**
 * POJO class that contains GUI elements of the server window and their corresponding get methods
 */
public class ServerGUI {
    private JPanel rootPanel;
    private JTextArea logText;
    private JLabel clientCounter;
    private JButton addClientButton;
    private JComboBox portList;
    private JButton startServerButton;
    private JLabel serverLabel;
    private JButton clearLogsButton;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JTextArea getLogText() {
        return logText;
    }

    public JLabel getClientCounter() {
        return clientCounter;
    }

    public JButton getAddClientButton() {
        return addClientButton;
    }

    public JComboBox getPortList() {
        return portList;
    }

    public JButton getStartServerButton() {
        return startServerButton;
    }

    public JLabel getServerLabel() {
        return serverLabel;
    }

    public JButton getClearLogsButton() {
        return clearLogsButton;
    }
}
