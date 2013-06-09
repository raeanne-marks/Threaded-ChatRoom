import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * ChatServer maintains a single server port and chat log for a chat room. It opens the port and listens for clients to connect. 
 * Upon connection, it initiates a thread for each client and reads all inputs from it, then transmitting the inputs to all other
 * client sockets and to the chat log.
 */

public class ChatServer {

	protected static volatile ArrayList<Socket> ConnectionArray = new ArrayList<Socket>(); //For threads to see all sockets.
	protected static PrintWriter FileOut; //Common PrintWriter to write to chat log from all threads

	public static void main(String[] args) throws IOException {

		ServerSocket server = new ServerSocket(8080);

		File file = new File("chatlog.txt"); 
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOut = new PrintWriter(new FileWriter(file.getAbsoluteFile()));		

		try {
			System.out.println("Waiting for clients...");
			
			while (true) {
				Socket sock = server.accept(); //accept a client
				System.out.println("I just had a client connect! :D");
				synchronized (ChatServer.ConnectionArray) {
					ConnectionArray.add(sock); 
				}

				ChatClient client = new ChatClient(sock);
				new Thread(client).start(); //Start a new ChatClient thread to handle the client. 
			}
		} finally {
			server.close();
			FileOut.close();
			ChatServer.FileOut.flush();
		}
	}
}