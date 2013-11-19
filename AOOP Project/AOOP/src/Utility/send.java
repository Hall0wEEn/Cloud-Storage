package Utility;

import java.io.*;
import java.net.Socket;

public class send implements Runnable {
	private Socket sock;
	private String serverName;
	private int port;
	private byte[] sha1hash;
	private byte[] bytes;
	private byte[] output;
	private char oc;

	public send(String serverName, int port, char oc, String input) {
		this(serverName, port, oc, input.getBytes());
	}

	send(String serverName, int port, char oc, File f) {
		this.serverName = serverName;
		this.port = port;
		this.oc = oc;
		try {
			bytes = new byte[(int) f.length()];
			FileInputStream fis = new FileInputStream(f);
			fis.read(bytes);
			fis.close();

			sha1hash = hash.sha1(bytes).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = new byte[sha1hash.length + 1 + bytes.length];                        // Constructing buffer
		System.arraycopy(sha1hash, 0, output, 0, sha1hash.length);
		output[40] = (byte) oc;
		System.arraycopy(bytes, 0, output, sha1hash.length + 1, bytes.length);
	}

	send(String serverName, int port, char oc, byte[] input) {
		this.serverName = serverName;
		this.port = port;
		this.oc = oc;
		bytes = input;
		try {
			sha1hash = hash.sha1(bytes).getBytes();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		output = new byte[sha1hash.length + 1 + bytes.length];                        // Constructing buffer
		System.arraycopy(sha1hash, 0, output, 0, sha1hash.length);
		output[40] = (byte) oc;
		System.arraycopy(bytes, 0, output, sha1hash.length + 1, bytes.length);
	}

	public void run() {
		try {
			while (true) {
				sock = new Socket(serverName, port);                            // Connecting to server
				System.out.println("Connecting to server");
				OutputStream outToServer = sock.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				out.writeInt(output.length);
				out.write(output);                                                // Sending out
				System.out.println("Sending out");
				InputStream inFromServer = sock.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);
				if (in.readUTF().equals("Okay")) {
					sock.close();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		File f = new File("C:\\Users\\Touch\\Desktop\\mul.py");
		try {
			(new Thread(new send("127.0.0.1", 4444, Utility.operationCode.UPLOAD, f))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
