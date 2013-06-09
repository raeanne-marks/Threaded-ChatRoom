import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * ChatClient implements and maintains a single client socket connecting to any server socket
 * for the sole intention of reading and writing simple messages inputted through the console.
 * It creates a new thread to handle listening while it handles getting user input and writing to
 * the socket. 
 */
public class ChatClient {

	static volatile StringBuilder builder = new StringBuilder();

	public static void main(String[] args) throws UnknownHostException, IOException {
		setSystem(); // set terminal to raw mode
		ChatClient client = new ChatClient();
		client.run();
	}

	public void run() throws UnknownHostException, IOException {

		Socket sock = new Socket("localhost", 8080);
		BufferedInputStream userIn = new BufferedInputStream(System.in);

		try {
			PrintStream out = new PrintStream(sock.getOutputStream());

			Listener listen = new Listener(sock);
			new Thread(listen).start();

			byte[] b = new byte[1]; // begin listener.
			while (true) {
				userIn.read(b);
				char c = (char) b[0];

				if (c == 127) { // delete key
					System.out.print('\r');
					for (int i = 0; i < (builder.length() + 3); i++) {
						System.out.print(" ");
					}
					if (builder.length() != 0) {
						builder.deleteCharAt(builder.length() - 1);
					}
					System.out.print('\r');
					System.out.print(builder);
				} else if (c == 3) { // control + c
					resetSystem(); //exit raw mode
					System.exit(0);
				} else if (c == 13) { // enter/return key
					System.out.print("\r" + builder.toString() + "  \n\r");
					out.print(builder.toString());
					builder = new StringBuilder();
					out.print('\n');
				} else {
					builder.append(c);
				}
				out.flush();
			}
		} finally {
			sock.close();
			userIn.close();
			System.out.println("About to exit raw mode!!!");
			resetSystem(); //exit raw mode
		}
	}

	public static void setSystem() throws IOException {
		String[] cmd = { "/bin/sh", "-c", "stty raw</dev/tty" };
		Runtime.getRuntime().exec(cmd);
	}

	public static void resetSystem() throws IOException {
		String[] cmd = { "/bin/sh", "-c", "stty -raw</dev/tty" };
		Runtime.getRuntime().exec(cmd);
	}
}
