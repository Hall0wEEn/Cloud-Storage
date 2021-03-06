package Client;

import Utility.hash;
import Utility.operationCode;
import Utility.queryParser;
import Utility.util;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class send implements Runnable {
	private static String serverName;
	private static int port;
	private String sha1hash;
	private static String slash;
	private static String TOKEN = "";
	private static Path HOME;
	private String uName;
	private char oc;
	private byte[] bytes;
	private byte[] output;
	private boolean stat = false;
	private String usedSpace = "";
	private static MenuItem menuItem;

	public boolean getStat() {
		return stat;
	}

	public String getUsedSpace() {
		return usedSpace;
	}

	public static void setMenuItem(MenuItem menuItem) {
		send.menuItem = menuItem;
	}

	public send(String serverName, int port, String s) {
		this.serverName = serverName;
		this.port = port;
		if (Utility.util.isWin())
			slash = "\\";
		else
			slash = "/";
		HOME = Paths.get(s);
	}

	public send(char oc, String input) {
		this(oc, input.getBytes(), "");
	}

	public send(char oc, File f) {
		this.oc = oc;
		this.uName = f.getAbsolutePath();
		try {
			if (f.length() > 512 * 1024)
				bytes = new byte[512 * 1024];
			else
				bytes = new byte[(int) f.length()];
			FileInputStream fis = new FileInputStream(f);
			fis.read(bytes);
			fis.close();

			sha1hash = hash.sha1(bytes);
			System.out.println(sha1hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utility.queryParser qp = new Utility.queryParser();
		try {
			qp.add("hash", sha1hash);
			qp.add("cIP", InetAddress.getLocalHost().getHostAddress());
			qp.add("session", TOKEN);
			qp.add("oc", Character.toString(oc));
			qp.add("fileName", uName.substring(HOME.toString().length()));
			qp.add("lastModified", String.valueOf(f.lastModified()));
			qp.add("lastbSize", String.valueOf(f.length() % (1024 * 512)));
			qp.add("totalBlock", String.valueOf(f.length() / (1024 * 512)));
			System.out.println(f.length() / 1024 / 512);

			qp.setContent(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = qp.getBytes();
	}

	public send(char oc) {
		this.oc = oc;

		if (oc == operationCode.ALLHASH) {

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
		} else if (oc == operationCode.HELO) {
			try {
				sha1hash = hash.sha1("HELO");
			} catch (Exception e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			Utility.queryParser qp = new Utility.queryParser();
			try {
				qp.add("hash", sha1hash);
				qp.add("cIP", InetAddress.getLocalHost().getHostAddress());
				qp.add("oc", Character.toString(oc));

				qp.setContent("HELO".getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
			output = qp.getBytes();
		}
	}

	public send(char oc, byte[] input, String session) {
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
				System.out.println("TOKEN : " + TOKEN);
//				System.out.println("Connecting to server");
				OutputStream outToServer = sock.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				out.writeInt(output.length);
				out.write(output);
				InputStream inFromServer = sock.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);

				if (oc == operationCode.LOGIN) {
					msg = in.readUTF();
					if (!msg.equals("Incorrect")) {
						stat = true;
						TOKEN = msg;
					}
					sock.close();
					break;
				} else if (oc == operationCode.REGISTER) {
					msg = in.readUTF();
					if (msg.equals("Okay")) {
						stat = true;
					}
					sock.close();
					break;
				} else if (oc == operationCode.LOGOUT) {
					TOKEN = "";
					sock.close();
					break;
				} else if (oc == operationCode.DOWNLOAD) {
					String hash = in.readUTF();
					long lastModified = in.readLong();
					File file = new File(HOME + slash + in.readUTF() + ".part");
					file.delete();
					byte[] content = new byte[512 * 1024];
					file.getParentFile().mkdirs();
					long lastbSize = in.readLong();
					long totalBlocks = in.readLong();
					BufferedOutputStream bos;
					long start;

					for (int i = 0; i <= totalBlocks; i++) {
						bos = new BufferedOutputStream(new FileOutputStream(file, true));
						if (i == totalBlocks)
							content = new byte[(int) lastbSize];
						in.readFully(content);
						start = System.nanoTime();
						bos.write(content);
						bos.flush();
						menuItem.setLabel("DL: " + String.format("%.2f", content.length * 1e9 / 1024 / 1024 / (System.nanoTime() - start)) + " MB/s");
						bos.close();
					}
					menuItem.setLabel("DL: 0 MB/s");
					file.setLastModified(lastModified);

					msg = in.readUTF();
					if (!Utility.hash.sha1(file).equals(hash))
						continue;

					String newName = file.getName().substring(0, file.getName().length() - 5);
					File newFile;
					newFile = new File(file.getParent() + slash + newName);
					file.renameTo(newFile);

					if (msg.equals("Okay")) {
						sock.close();
						break;
					}
				} else if (oc == operationCode.UPLOAD) {
					File f = new File(uName);
					System.out.println(f.length());
					FileInputStream fis = new FileInputStream(f);
					byte[] bytes = new byte[512 * 1024];
					int count;
					long start;
					while ((count = fis.read(bytes)) != -1) {
						start = System.nanoTime();
						out.write(bytes, 0, count);
						out.flush();
						menuItem.setLabel("UL: " + String.format("%.2f", bytes.length * 1e9 / 1024 / 1024 / (System.nanoTime() - start)) + " MB/s");
					}
					menuItem.setLabel("UL: 0 MB/s");
					fis.close();
					msg = in.readUTF();
					if (msg.equals("Okay")) {
						sock.close();
						break;
					}
				} else if (oc == operationCode.ALLHASH) {
					Thread t;
					msg = in.readUTF();
					String[] lines = msg.split("\n");
					if (msg.equals(""))
						break;
					String[] tmp;
					String fileName;
					long lastModified;
					File file;
					for (String line : lines) {
						tmp = line.split(" ");
						lastModified = Long.parseLong(tmp[1]);
						fileName = line.substring(line.indexOf(' ', line.indexOf(' ') + 1) + 1);
						file = new File(HOME + slash + fileName);

						if (file.exists() && (file.getName().charAt(0) == '.' || hash.sha1(file).equals(tmp[0])))
							continue;

						if (util.getExt(file).equals("part")) {
							t = (new Thread(new send(operationCode.UPLOAD, new File(file.toString()))));
							t.start();
							t.join();
						} else if (file.lastModified() > lastModified) {
							t = (new Thread(new send(operationCode.UPLOAD, new File(file.toString()))));
							t.start();
							t.join();
						} else {
							t = (new Thread(new send(operationCode.DOWNLOAD, fileName)));
							t.start();
							t.join();
						}
					}

					String[] locallines = Utility.hash.allFiles(HOME).split("\n");
					List<String> list = new ArrayList(Arrays.asList(locallines));
					for (String line : lines) {
						if (list.contains(line))
							list.remove(line);
					}

					for (String line : list) {
						tmp = line.split(" ");
						fileName = line.substring(line.indexOf(' ', line.indexOf(' ') + 1) + 1);
						System.out.println(fileName);
						file = new File(HOME + slash + fileName);

						if (file.getName().charAt(0) == '.')
							continue;

						if (util.getExt(file).equals("part")) {
							t = (new Thread(new send(operationCode.DOWNLOAD, fileName)));
							t.start();
							t.join();
						} else {
							t = (new Thread(new send(operationCode.UPLOAD, new File(file.toString()))));
							t.start();
							t.join();
						}
					}

					msg = in.readUTF();
					if (msg.equals("Okay")) {
						sock.close();
						break;
					}

				} else if (oc == operationCode.SPACE) {
					usedSpace = in.readUTF();

					msg = in.readUTF();
					if (msg.equals("Okay")) {
						sock.close();
						break;
					}
				} else {
					stat = true;
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
