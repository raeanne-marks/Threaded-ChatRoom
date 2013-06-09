import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * Listener manages a thread which listens in a socket and prints to the console any simple text inputs.
 */
public class Listener implements Runnable {
	Socket sock;
	
	public Listener (Socket x) {
		this.sock = x;
	}
	
	public void run () {
		
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
			
			while (true) {				
				String message = in.readLine();
				if (message == null) {
					Thread.currentThread().interrupt();
					break;
				}
				
				String userBuffer = ChatClient.builder.toString();
				int length = userBuffer.length();
				System.out.print("\r");
				for (int i = 0; i < length; i++) {
					System.out.print(" ");
				}
				System.out.println("\r" + message);
				System.out.print('\r' + userBuffer);
			}
		}
		catch (Exception x) {
			x.printStackTrace();
			System.out.println(x);
		}
	}
}
