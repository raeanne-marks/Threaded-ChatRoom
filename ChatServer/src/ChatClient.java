import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/*
 * ChatClient manages a thread which controls the connection of a particular client socket to the server. It obtains the username
 * from the client, then continuously reads all inputs from the client socket. For every input message, it broadcasts the message
 * to all client sockets connected to the server, as listed in the ConnectionArray in the ChatServer class as well as writes to the 
 * common chat log created in the ChatServer class. It prints appropriate connection and disconnection messages when the client connects
 * and disconnects. It provides appropriate locks on the shared data from the ChatServer class to make them thread-safe. 
 */
public class ChatClient implements Runnable {

	private Socket sock;

	public ChatClient(Socket x) {
		this.sock = x;
	}

	public void run() {

		try {

			PrintStream outUser = new PrintStream(this.sock.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));

			outUser.println("Enter your username: ");
			String username = in.readLine();
			transmitMessage(username + " has joined the chatroom!");

			while (!Thread.interrupted()) { 

				String message = in.readLine();
				System.out.println("Hey, I just received this: " + message);

				if (message == null) { //If the client has disconnected, the message will be null.
					
					synchronized (ChatServer.ConnectionArray) {
						ChatServer.ConnectionArray.remove(this.sock);
						}
					
					transmitMessage(username + " has disconnected."); //Broadcast to all clients

					Thread.currentThread().interrupt(); //die
					break;
				}

				transmitMessage(username + ": " + message); //Still connected, broadcast message.
			}

		} catch (Exception e) {

			synchronized (ChatServer.ConnectionArray) {
				ChatServer.ConnectionArray.remove(ChatServer.ConnectionArray.indexOf(this.sock));
			}
			
			System.out.println(e);
			Thread.currentThread().interrupt();
		}

	}

	/*
	 * transmitMessage accepts a string message and broadcasts it to all sockets connected to the server, as well
	 * as prints the message to the chat log. It implements locks on the ConnectionArray and FileOut objects from
	 * the ChatServer class to ensure thread-safety.
	 */
	private void transmitMessage(String message) throws IOException {

		synchronized (ChatServer.ConnectionArray) { //broadcast to users
			for (int i = 0; i < ChatServer.ConnectionArray.size(); i++) {
				Socket tempSock = ChatServer.ConnectionArray.get(i);
				PrintStream out = new PrintStream(tempSock.getOutputStream());
				out.println(message);
			}
		}

		synchronized (ChatServer.FileOut) { //print to log
			ChatServer.FileOut.println(message);
			ChatServer.FileOut.flush();
		}
	}

}
