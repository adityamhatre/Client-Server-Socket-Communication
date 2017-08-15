import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.io.IOException
import java.util.*
import javax.swing.*


//REFERENCES:
/**
 * https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
 * https://antonioleiva.com/kotlin-android-extension-functions
 */

/**
 * Created by Aditya Mhatre on 12 Jun, 2017.
 * MavID: 1001429814
 */


/**
 * Server class that is responsible for starting the server and accept incoming client requests
 */
class Server {
    /* The client socket that is requesting to connect to server */
    private var socket: Socket? = null

    /* Boolean to keep track whether the server is running or not */
    private var serverRunning = false

    /* The port number at which the server will accept incoming connections */
    private var portNumber: Int? = null

    /**
     * Companion object is the "companion" of the specified class
     * It acts similar to the Java static initializer
     */
    companion object {
        /* The lexicon (List of words) provided to check against incoming client requests */
        val lexicon: List<String> = listOf<String>("aditya", "rajesh", "mhatre")

        /* The GUI class of the server */
        val serverGUI = ServerGUI()

        /* A counter to keep track of the number of connected clients */
        var clientCounter: Int = 0

        /* A vector list of all the ports that can be used and to be shown in the drop-down field */
        val portListValues: Vector<Any> = Vector()

        /* The server socket that accepts incoming socket requests and which runs on the specified port */
        var serverSocket: ServerSocket? = null
    }


    /**
     * init block is like the constructor in Java.
     * This block is invoked whenever the class is instantiated
     */
    init {
        for (i in 1025..32767) { // or for(i in 1025.range(32767))
            Server.portListValues.add(i)    // Adding the values of the variable 'i' in the port list
        }
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) // Setting the desired look and feel i.e., the theme of the GUI

        /* The actual window that would be displayed when the class is invoked */
        val serverWindow = JFrame()

        /**
         * Setting the server GUI window properties
         */
        serverWindow.title = "Server"
        serverWindow.contentPane = serverGUI.rootPanel  /* Displays 'rootPanel' in the window */
        serverWindow.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE  /*  */
        serverWindow.setSize(800, 600)
        serverWindow.isResizable = false

        serverGUI.clientCounter.text = "Connected Clients: ${Server.clientCounter}"

        /**
         * Setting the click event listener for the "Add Client" button which will start a new Client window if the server is running
         * else will display a message dialog
         */
        serverGUI.addClientButton.addActionListener {
            if (serverRunning)
                Client()
            else JOptionPane.showMessageDialog(null, "You need to start the server first")
        }

        serverGUI.portList.model = DefaultComboBoxModel(portListValues)

        /**
         * Setting the click event listener for the "Start Server" button which will start the server if it's not running
         * else will stop the server
         */
        serverGUI.startServerButton.addActionListener {
            if (!serverRunning) {
                serverGUI.startServerButton.text = "Stop Server"
                portNumber = serverGUI.portList.selectedItem.toString().toInt() /* Getting the selected port number value from the drop-down list */

                /** Starts the server to accept incoming connections in a separate thread so that it wouldn't block the UI thread
                 * Uses lambda function */
                Thread({ startServer() }).start()
            } else {
                serverGUI.startServerButton.text = "Start Server"
                serverSocket?.close()
            }
        }

        /**
         * Setting the click event listener for the "Clear Logs" button which will clear the log window in server
         */
        serverGUI.clearLogsButton.addActionListener { serverGUI.logText.text = "" }

        serverWindow.isVisible = true
        serverWindow.setLocationRelativeTo(null)
    }


    /**
     * Starts the server to accept incoming connections
     */
    fun startServer() {
        try {
            println("Starting server at port $portNumber...\nWaiting for clients...")
            serverGUI.logText.concatText("Starting server at port $portNumber...\nServer started at port ${portNumber}\nWaiting for clients...\n")
            serverGUI.serverLabel.text = "Server is running..."
            serverSocket = ServerSocket(portNumber!!)   /* Starting the server socket with the selected port number */
            serverRunning = true
        } catch (e: Exception) {
            e.printStackTrace()
            serverRunning = false
        }

        /**
         * Listen for incoming connections indefinitely until the server socket is closed
         */
        while (true) {
            try {
                socket = serverSocket?.accept() /* Accept the incoming client socket to connect it with server socket */
                ClientThread(socket!!).start()  /* Start a new thread with the current client socket */
            } catch (e: Exception) {
                serverRunning = false
                serverGUI.serverLabel.text = "Server is not running"
                serverGUI.logText.concatText("Server stopped\n")
                clientCounter = 0
                serverGUI.clientCounter.text = "Connected Clients: $clientCounter"
                break
            }
        }


    }

}

/**
 * Separate thread for handling each client requests making the server "multi-threaded"
 * @param socket Socket of the connecting client
 */
class ClientThread(val socket: Socket) : Thread() {

    /* The input stream of the socket that carries the data from client to server */
    private var inputStream: InputStream? = null

    /* Buffered Reader to read the input stream of the socket */
    private var bufferedReader: BufferedReader? = null

    /* The output stream of the socket that will carry the data from server to client */
    private var dataOutputStream: DataOutputStream? = null


    /**
     * The runnable function of the thread that carries out the actual execution that the thread is supposed to do
     * In this case, reading from incoming socket and processing the string and returning back the processed text from server to client
     */
    override fun run() {
        super.run()
        println("Connected ${socket.localSocketAddress}")
        Server.clientCounter++  // Increments the client counter, since a new client is connected
        Server.serverGUI.clientCounter.text = "Connected Clients: ${Server.clientCounter}"
        Server.serverGUI.logText.concatText("Client Connected\n")

        try {
            /**
             * Initialise the parameters
             */
            inputStream = socket.getInputStream()
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            dataOutputStream = DataOutputStream(socket.getOutputStream())
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        var line: String?   // A temporary variable to store each line of the input from socket to use in while loop
        var sendToClient: String    // A temporary variable to store the processed string returned by the method call
        while (true) {
            try {
                line = bufferedReader?.readLine()   // Reading rhe line
                println("received in server: $line")
                /**
                 * IF-ELSE to check if the read line is null else carry on the task
                 */
                if (line == null) {
                    socket.close()  // Closing the socket of the client since there is no more data
                    Server.serverGUI.logText.concatText("Client disconnected\n")
                    return
                } else {
                    Server.serverGUI.logText.concatText("\nClient request: $line\n")
                    sendToClient = mainLogic(line)  // Call to the method to process the string
                    Server.serverGUI.logText.concatText("Server response: $sendToClient\n\n")
                    println("sending to client: $sendToClient")
                    dataOutputStream?.writeChars("$sendToClient\n\r")   // Writing characters to the data output stream of the socket to send back to client
                    dataOutputStream?.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
    }

    /**
     * The primary logic of the project to check the given string words with the provided lexicon
     * @param blockOfText String to be checked
     *
     * @return The processed string that is checked against the lexicon
     */
    private fun mainLogic(blockOfText: String): String {
        var returnString = ""   // The variable containing the processed string to be returned

        /**
         * Function to split the block of text in words and then process them one by one
         * forEach is a predefined function in kotlin that acts upon a list or array and processes the elements one by one
         */
        blockOfText.split(" ").forEach {
            /**
             * 'it' is an iteration variable provided by the Kotlin language
             * In this case, the variable contains the word of the block of text
             */
            if (Server.lexicon.contains(it)) {  // If the current word is present in the lexicon, add it in the string to be returned
                returnString = returnString.plus(it).plus(" ")
            } else {    // else add "<ERROR>" in the string that is to be returned
                returnString = returnString.plus("<ERROR>").plus(" ")
            }
        }
        return returnString.trim()
    }

}

/**
 * Extension function for JTextArea to concat string
 * Kotlin extension functions lets us add new functions to existing classes.
 *
 * @param text String to be concatenated
 */
private fun JTextArea.concatText(text: String) {
    this.text = this.text + text    // Concats the given text to the already present text in the JTextArea
}

/**
 * Starts the execution of this file
 *
 * @param args Arguments to be passed for execution
 **/
fun main(args: Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) // Setting the desired look and feel i.e., the theme of the GUI

    SwingUtilities.invokeLater {
        Server()    // Call the init function to start the program
    }
}