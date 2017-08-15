import java.net.InetAddress
import java.net.Socket
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.ConnectException
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.WindowConstants


/**
 * Created by Aditya Mhatre on 12 Jun, 2017.
 * MavID: 1001429814
 */


/**
 * Client class that initializes the client GUI and contains other functions like connecting to server and sending data
 * from client to server.
 */
class Client {

    /* Socket of the client that is to be connected to the server */
    private var socket = Socket()

    /* The GUI class of the client window */
    val clientGUI = ClientGUI()

    /* The boolean variable to determine if the client is connected to the server or not */
    var connected = false


    init {
        /* The actual window that would be displayed when the class is invoked */
        val clientWindow = JFrame()


        /**
         * Setting the client GUI window properties
         */
        clientWindow.title = "Client"
        clientWindow.contentPane = clientGUI.rootPanel
        clientWindow.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        clientWindow.setSize(800, 600)
        clientWindow.isResizable = false
        clientWindow.isVisible = true
        clientWindow.pack()

        clientWindow.setLocationRelativeTo(null)


        /**
         * Adding a window listener so as to disconnect the server-client connection on exiting the client window
         */
        clientWindow.addWindowListener(object : java.awt.event.WindowAdapter() {
            override fun windowClosing(windowEvent: java.awt.event.WindowEvent?) {
                if (JOptionPane.showConfirmDialog(clientWindow, "Are you sure to close this window?", "Confirm action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    if (connected) {
                        disconnect()
                        clientWindow.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
                    }
                    super.windowClosing(windowEvent)
                } else clientWindow.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
            }
        })

        /**
         * Sends the input text to the server if the server is running and if the client is connected to it
         */
        clientGUI.sendButton.addActionListener {
            if (connected) {
                if (clientGUI.inputText.text.isNotEmpty())
                    if (!Server.serverSocket?.isClosed!!) {
                        sendToServer(clientGUI.inputText.text)
                    } else JOptionPane.showMessageDialog(null, "You need to start server first")
            } else JOptionPane.showMessageDialog(null, "You need to connect to server first")
        }

        /**
         * Connects to the server if the server is running or disconnects from the connected server
         */
        clientGUI.connectButton.addActionListener {
            if (!connected) {
                if (Server.clientCounter < 2) {
                    if (!Server.serverSocket?.isClosed!!) {
                        val portNumber = getPortNumber()
                        if (portNumber != -1)
                            try {
                                socket = Socket(InetAddress.getLocalHost(), portNumber)
                                connected = true
                                clientGUI.statusLabel.text = "Status: Connected"
                                clientGUI.connectButton.text = "Disconnect from server"

                                /**
                                 * Starts the background thread to check server status
                                 */
                                ServerChecker(this).start()

                            } catch (e: ConnectException) {
                                JOptionPane.showMessageDialog(null, "Cannot connect to port $portNumber")
                                connected = false
                                clientGUI.statusLabel.text = "Status: Disconnected"
                                clientGUI.connectButton.text = "Connect to server"
                            }

                    } else JOptionPane.showMessageDialog(null, "You need to start server first")
                } else {
                    JOptionPane.showMessageDialog(null, "Maximum number of connected clients reached")
                }
            } else {
                disconnect()
            }

        }
    }


    /**
     * Function to prompt user to enter the port number of the server in order to connect to it
     * @return The port number or -1 if bad input
     */
    private fun getPortNumber(): Int {
        val portNumber: Any?
        try {
            portNumber = JOptionPane.showInputDialog("Enter port number")
            if (portNumber == null) {
                return -1
            } else {
                return portNumber.toString().toInt()
            }
        } catch (nfe: NumberFormatException) {
            JOptionPane.showMessageDialog(null, "Invalid port number")
            connected = false
            clientGUI.statusLabel.text = "Status: Disconnected"
        }

        return -1
    }

    /**
     * Disconects from the connected server
     */
    private fun disconnect() {
        socket.close()
        connected = false
        Server.clientCounter--
        Server.serverGUI.clientCounter.text = "Connected Clients: ${Server.clientCounter}"
        clientGUI.statusLabel.text = "Status: Disconnected"
        clientGUI.connectButton.text = "Connect to server"
    }

    /**
     * Function to send the input text from client to server
     * @param textToSend is the string that user has entered in the input field
     */
    fun sendToServer(textToSend: String) {
        val outputStream = socket.getOutputStream()
        val printWriter = PrintWriter(outputStream, true)

        // receiving from server ( serverBufferedReader  object)
        val inputStream = socket.getInputStream()
        val serverBufferedReader = BufferedReader(InputStreamReader(inputStream))

        val receiveMessage: String

        printWriter.println(textToSend)         // sending to server
        printWriter.flush()                      // flush the data
        println("\nsending from client: $textToSend")

        receiveMessage = serverBufferedReader.readLine()
        if (receiveMessage != null) {        // receive from server
            println("back from server: $receiveMessage")
            clientGUI.outputText.text = receiveMessage
            disconnect()
        }
    }
}


/**
 * A worker thread class to check if the server is running or not
 */
class ServerChecker(val client: Client) : Thread() {
    override fun run() {
        super.run()
        println("server checker started")
        while (true) {  // Check indefinitely
            if (Server.serverSocket?.isClosed!!) {  // Condition to check server running status
                client.clientGUI.statusLabel.text = "Status: Disconnected"
                Server.clientCounter--
                client.connected = false
                Server.serverGUI.clientCounter.text = "Connected Clients: ${Server.clientCounter}"
                break   // Stops the thread
            }
        }
    }
}