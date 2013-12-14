package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class serverSocket {
	static ServerSocket socket;
	static int port = 4444;

	public static void main(String[] args) {
		try {
			socket = new ServerSocket(port);
			System.out.println("Bound to port: " + port);
		} catch (IOException e) {
			System.out.println("Cannot bind to port: " + port);
			System.exit(0);
		}

		sessionManager sm = new sessionManager();
		System.out.println(System.getProperty("user.home"));

		while (true) {
			try {
				Socket s = socket.accept();
				System.out.println("New Client: " + s.getInetAddress().toString());
				(new Thread(new clientHandler(s, sm, "/Users/Touch/Desktop/cloud"))).start();
			} catch (Exception e) {
				System.out.println("Failed to accept client");
			}
		}
	}
}
