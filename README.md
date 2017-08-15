A Spelling Checker Client & Server
-------------------------------------------------------------------------
This is a client/server system that will check the spelling in a block of text sent by a client which is connected to a server using the provided lexicon. The server can be connected by multiple clients at the same time by making the server multi--threaded with a single thread for each client.


Getting Started
--------------------------------------------------------------------------
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
1. Download the adityamhatre_1001429814.zip
2. Unzip the file to get two folders viz., source and binary.
3. Open the binary folder to get the .jar file of the project.
	3a. Run the file to open up the server window
	3b. Rest of the instructions are below
4. Open the source folder to get the project folder which contains the source files of the project.
	4a. Download the IntelliJ IDEA Community Edition IDE to open the project from the Open Menu of the IDE from https://www.jetbrains.com/idea/download/#section=windows
	4b. Make sure it includes the "Kotlin" plugin since the source code is in Kotlin language.

Prerequisites
---------------------------------------------
1. Java 1.8
2. Windows 10

Instructions to run the project
--------------------------------------------
1. Select a port number from the drop-down.
2. Select the "Start Server" button to start the server.
3. Wait for the server to start.
4. Select the "Add Client" button to launch a new client window.
	4a. You can add up to 10 clients at the same time.
5. Select the "Connect" button to connect to the server and enter the server's port number to connect to it.
6. Enter some text in the input field and select "Send" button.
7. Wait for the server to reply.
8. The response from the server is displayed in the appropriate client window.
9. The client will then get disconnected automatically.
10. Close the client window(s).
11. Select the "Stop Server" button to stop the server.
12. Exit the GUI.

Bugs
-----------------------
1. The GUI theme isn't native windows.

Assumptions
---------------------------------
1. The maximum number of clients is arbitrarily declared as 10 to make the program memory-safe and to avoid any stack overflow errors.
