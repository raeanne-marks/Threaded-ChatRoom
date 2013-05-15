import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*
 * ChatClient implements and maintains a single client socket connecting to any server socket
 * for the sole intention of reading and writing simple messages inputted through the console.
 * It creates a new thread to handle listening while it handles getting user input and writing to
 * the socket. 
 */
public class ChatClient {
	
	public static void main (String[] args) throws UnknownHostException, IOException {
		ChatClient client = new ChatClient();
		client.run();
	}

	public void run () throws UnknownHostException, IOException {
		
		Socket sock = new Socket("localhost", 8080);
		Scanner userIn = new Scanner (System.in);
		
		try {
			PrintStream out = new PrintStream (sock.getOutputStream());
			
			Listener listen = new Listener(sock);
			new Thread(listen).start(); //begin listener.
			
			while (true) {
				String message = userIn.nextLine();
				out.println(message);
				out.flush();
			}
		}
		finally {
			sock.close();
			userIn.close();
		}		
	}	
}
