package Utility;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class newSend implements Runnable {
	private Socket sock;
	private String serverName;
	private int port;
	private String sha1hash;
	private byte[] bytes;
	private byte[] output;
	private char oc;

	public newSend(String serverName, int port, char oc, String input) {
		this(serverName, port, oc, input.getBytes());
	}

	newSend(String serverName, int port, char oc, File f) {
		this.serverName = serverName;
		this.port = port;
		this.oc = oc;
		try {
			bytes = new byte[(int) f.length()];
			FileInputStream fis = new FileInputStream(f);
			fis.read(bytes);
			fis.close();

			sha1hash = hash.sha1(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		queryParser qp = new queryParser();
		try {
			qp.add("hash", sha1hash);
			qp.add("cIP", InetAddress.getLocalHost().getHostAddress());
			qp.add("session", Character.toString(oc));
			qp.add("oc", Character.toString(oc));
			qp.add("fileName", "test.txt");
			qp.add("blockNo", "1");
			qp.add("totalBlock", "10");
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = qp.getBytes();
	}

	newSend(String serverName, int port, char oc, byte[] input) {
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
		String msg;
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
				msg = in.readUTF();
				if (msg.equals("Okay")) {
					sock.close();
					break;
				} else if (msg.equals("Again")) {
					continue;
				} else {
					System.out.println(msg);
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
