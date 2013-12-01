package Utility;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class newSend implements Runnable {
	private Socket sock;
	private String serverName;
	private int port;
	private String sha1hash;
	private static String token = "";
	private char oc;
	private byte[] bytes;
	private byte[] output;

	public newSend(String serverName, int port, char oc, String input, String session) {
		this(serverName, port, oc, input.getBytes(), session);
	}

	public newSend(String serverName, int port, char oc, File f) {
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
			qp.add("session", token);
			qp.add("oc", Character.toString(oc));
			qp.add("fileName", f.getName());
			qp.add("totalBlock", String.valueOf((f.length() / 1024) / 1024));

			qp.setContent(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = qp.getBytes();
	}

	public newSend(String serverName, int port, char oc, byte[] input, String session) {
		this.serverName = serverName;
		this.port = port;
		this.oc = oc;
		try {
			sha1hash = hash.sha1(input);
//			System.out.println(sha1hash);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		queryParser qp = new queryParser();
		try {
			qp.add("hash", sha1hash);
			qp.add("cIP", InetAddress.getLocalHost().getHostAddress());
			if (!session.equals(""))
				qp.add("session", token);
			qp.add("oc", Character.toString(oc));

			qp.setContent(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = qp.getBytes();
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
				System.out.println("Token : " + token);
				if (oc == operationCode.LOGIN) {
					msg = in.readUTF();
					if (!msg.equals("Incorrect")) {
						token = msg;
						sock.close();
						break;
					}
				} else if (oc == operationCode.LOGOUT) {
					token = "";
				} else if (oc == operationCode.DOWNLOAD) {

				} else if (oc == operationCode.UPLOAD) {
					msg = in.readUTF();
					if (msg.equals("Okay")) {
						sock.close();
						break;
					}
				} else {
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
