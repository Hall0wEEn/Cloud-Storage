package Client;

import Utility.hash;
import Utility.operationCode;
import Utility.queryParser;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class send implements Runnable {
	private String serverName;
	private int port;
	private String sha1hash;
	private static String slash;
	private static String TOKEN = "";
	private static Path HOME;
	private char oc;
	private byte[] bytes;
	private byte[] output;

	public send(String s) {
		if (Utility.util.isWin())
			slash = "\\";
		else
			slash = "/";
		HOME = Paths.get(s);
	}

	public send(String serverName, int port, char oc, String input, String session) {
		this(serverName, port, oc, input.getBytes(), session);
	}

	public send(String serverName, int port, char oc, File f) {
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
		Utility.queryParser qp = new Utility.queryParser();
		try {
			qp.add("hash", sha1hash);
			qp.add("cIP", InetAddress.getLocalHost().getHostAddress());
			qp.add("session", TOKEN);
			qp.add("oc", Character.toString(oc));
			qp.add("fileName", f.getName());
			qp.add("lastModified", String.valueOf(f.lastModified()));
			qp.add("totalBlock", String.valueOf((f.length() / 1024) / 1024));

			qp.setContent(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = qp.getBytes();
	}

	public send(String serverName, int port, char oc) {
		this.serverName = serverName;
		this.port = port;
		this.oc = oc;
		try {
			sha1hash = hash.sha1("ALLHASH");
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Utility.queryParser qp = new Utility.queryParser();
		try {
			qp.add("hash", sha1hash);
			qp.add("cIP", InetAddress.getLocalHost().getHostAddress());
			qp.add("session", TOKEN);
			qp.add("oc", Character.toString(oc));

			qp.setContent("ALLHASH".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = qp.getBytes();
	}

	public send(String serverName, int port, char oc, byte[] input, String session) {
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
				qp.add("session", TOKEN);
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
				Socket sock = new Socket(serverName, port);
				System.out.println("Connecting to server");
				OutputStream outToServer = sock.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				out.writeInt(output.length);
				out.write(output);                                                // Sending out
				System.out.println("Sending out");
				InputStream inFromServer = sock.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);
				System.out.println("Token : " + TOKEN);
				if (oc == operationCode.LOGIN) {
					msg = in.readUTF();
					if (!msg.equals("Incorrect")) {
						TOKEN = msg;
						sock.close();
						break;
					}
				} else if (oc == operationCode.LOGOUT) {
					TOKEN = "";
				} else if (oc == operationCode.DOWNLOAD) {

				} else if (oc == operationCode.UPLOAD) {
					msg = in.readUTF();
					if (msg.equals("Okay")) {
						sock.close();
						break;
					}
				} else if (oc == operationCode.ALLHASH) {
					msg = in.readUTF();
					String[] lines = msg.split("\n");
					String[] tmp;
					String hash;
					long lastModified;
					File file;
					for (String line : lines) {
						tmp = line.split(" ");
						hash = tmp[0];
						lastModified = Long.parseLong(tmp[1]);
						file = new File(HOME + slash + tmp[2]);

						if (file.lastModified() > lastModified)
							(new Thread(new send("127.0.0.1", 4444, operationCode.UPLOAD, new File(file.toString())))).start();
						else
							(new Thread(new send("127.0.0.1", 4444, operationCode.DOWNLOAD, new File(tmp[3])))).start();
					}

					break;
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
}
