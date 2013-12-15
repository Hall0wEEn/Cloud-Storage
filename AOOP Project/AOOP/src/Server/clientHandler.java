package Server;

import Utility.hash;
import Utility.operationCode;
import Utility.queryParser;
import Utility.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class clientHandler implements Runnable {
	Socket socket;
	DataInputStream in = null;
	DataOutputStream out = null;
	sessionManager sm;
	Path HOME;
	static DefaultTableModel model;
	static JTextArea text;

	clientHandler(Socket sock, sessionManager sm, String path) {
		socket = sock;
		this.sm = sm;
		this.HOME = Paths.get(path);
	}

	public static void setModel(DefaultTableModel model) {
		clientHandler.model = model;
	}

	public static void setText(JTextArea text) {
		clientHandler.text = text;
	}

	public String addDate() {
		java.util.Date date = new java.util.Date();
		return (new Timestamp(date.getTime())).toString() + " ";
	}

	private void addClient(String user, String ip, boolean sync, double space) {
		String[] rowData = {user, ip, "IDLE", String.format("%.2f", space) + " MB"};
		model.addRow(rowData);
		changeStage(user, sync);
	}

	private void removeClient(String user) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (user.equals(model.getValueAt(i, 0))) {
				model.removeRow(i);
			}
		}
	}

	private void changeSpace(String user, double space) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (user.equals(model.getValueAt(i, 0))) {
				model.setValueAt(String.format("%.2f", space) + " MB", i, 3);
			}
		}
	}

	private void changeStage(String user, boolean sync) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (user.equals(model.getValueAt(i, 0))) {
				if (sync) {
					model.setValueAt("Syncing", i, 2);
				} else {
					model.setValueAt("IDLE", i, 2);
				}
			}
		}
	}

	private void delete(File file) {
		if (file.isDirectory())
			for (File tmp : file.listFiles())
				delete(tmp);
		else
			file.delete();
	}

	@Override
	public void run() {
		String data;
		String tmp;
		byte[] content;
		String user = "", passwd = "";
		Connection connection;
		Statement statement;
		ResultSet rs;

		String retpassword = "";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Just connected to " + socket.getRemoteSocketAddress());
			int byteLength = in.readInt();
			byte[] bytes = new byte[byteLength];
			in.readFully(bytes);

			queryParser qp = new queryParser(bytes);
			content = qp.getContent();
			char oc = qp.get("oc").charAt(0);
			String recvHash = qp.get("hash");

			if (hash.sha1(content).equals(recvHash)) {
//				out.writeUTF("Okay");
//				File f = new File("C:\\Users\\Touch\\Desktop\\test2.rar");
//				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
//				bos.write(content);
//				bos.flush();
//				bos.close();

				switch (oc) {
					case operationCode.HELO:
						out.writeUTF("Okay");
						break;
					case operationCode.REGISTER:

						data = new String(content, "UTF-8");
						for (int i = 0; i < data.length(); i++) {
							if (data.charAt(i) == '|') {
								user = data.substring(0, i);
								passwd = data.substring(i + 1, data.length());
							}
						}

						try {
							connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cloud", "auth", "imm@ninja");
						} catch (SQLException e) {
							e.printStackTrace();
							return;
						}

						statement = connection.createStatement();

						rs = statement.executeQuery("select password from users where user='" + user + "';");
						if (rs.next()) {
							out.writeUTF("Username is not available");
							break;
						}

						tmp = "insert into users values(\"" + user + "\", \"" + passwd + "\");";
						statement.executeUpdate(tmp);
						File dir;
						if (util.isWin())
							dir = new File(HOME.toString() + "\\" + user);
						else
							dir = new File(HOME.toString() + "/" + user);
						if (!dir.exists())
							dir.mkdir();
						out.writeUTF("Okay");

						break;
					case operationCode.LOGIN:

						data = new String(content, "UTF-8");
						for (int i = 0; i < data.length(); i++) {
							if (data.charAt(i) == '|') {
								user = data.substring(0, i);
								passwd = data.substring(i + 1, data.length());
							}
						}

						try {
							connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cloud", "auth", "imm@ninja");
						} catch (SQLException e) {
							e.printStackTrace();
							return;
						}

						statement = connection.createStatement();
						rs = statement.executeQuery("select password from users where user='" + user + "';");

						while (rs.next())
							retpassword = rs.getString("password");

						if (passwd.equals(retpassword)) {
							System.out.println("Login successfully");
							sm.createSession(user);
							out.writeUTF(sm.getSessioin(user));
						} else {
							out.writeUTF("Incorrect");
							break;
						}

						Path userPath;
						File file;
						if (util.isWin()) {
							userPath = Paths.get(HOME.toString() + "\\" + user);
							file = new File(userPath + "\\");
						} else {
							userPath = Paths.get(HOME.toString() + "/" + user);
							file = new File(userPath + "/");
						}

						long usedSpace = util.folderSize(file);

						addClient(user, qp.get("cIP"), false, usedSpace / 1000000.00);
						text.append(addDate() + user + " is logging in.\n");
						break;
					case operationCode.LOGOUT:
						user = sm.check(Integer.parseInt(qp.get("session")));
						sm.destroySession(user);
						removeClient(user);
						text.append(addDate() + user + " is logging out.\n");
						break;
					case operationCode.UPLOAD:
						user = sm.check(Integer.parseInt(qp.get("session")));
						changeStage(user, true);
						if (util.isWin()) {
							userPath = Paths.get(HOME.toString() + "\\" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "\\" + qp.get("fileName") + ".part");
						} else {
							userPath = Paths.get(HOME.toString() + "/" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "/" + qp.get("fileName") + ".part");
						}

						file.getParentFile().mkdirs();
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//						System.out.println(hash.sha1(content));
//						bos.write(content);
//						bos.flush();
//						bos.close();

						int totalBlock = Integer.parseInt(qp.get("totalBlock"));
						System.out.println(totalBlock);
						byte[] tmpBytes = new byte[512 * 1024];
						for (int i = 0; i <= totalBlock; i++) {
							if (i == totalBlock)
								tmpBytes = new byte[Integer.parseInt(qp.get("lastbSize"))];
							System.out.println(i + 1 + " " + tmpBytes.length);
							in.readFully(tmpBytes);
							bos = new BufferedOutputStream(new FileOutputStream(file, true));
							bos.write(tmpBytes);
							bos.flush();
							bos.close();
						}
						file.setLastModified(Long.parseLong(qp.get("lastModified")));
						String newName = file.getName().substring(0, file.getName().length() - 5);
						File newFile;
						if (util.isWin())
							newFile = new File(file.getParent() + "\\" + newName);
						else
							newFile = new File(file.getParent() + "/" + newName);

						file.renameTo(newFile);
						text.append(addDate() + user + " is uploading " + newFile + ".\n");

						out.writeUTF("Okay");

						user = sm.check(Integer.parseInt(qp.get("session")));
						usedSpace = util.folderSize(userPath.toFile());
						System.out.println("Change to " + usedSpace);
						changeSpace(user, usedSpace / 1000000.00);

						changeStage(user, false);
						break;
					case operationCode.DOWNLOAD:
						String fileName = new String(qp.getContent());
						user = sm.check(Integer.parseInt(qp.get("session")));
						changeStage(user, true);
						if (util.isWin()) {
							userPath = Paths.get(HOME.toString() + "\\" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "\\" + fileName);
						} else {
							userPath = Paths.get(HOME.toString() + "/" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "/" + fileName);
						}

						text.append(addDate() + user + " is downloading " + file + ".\n");

						out.writeUTF(hash.sha1(file));
						out.writeLong(file.lastModified());
						out.writeUTF(fileName);
						out.writeLong(file.length() % (1024 * 512));
						out.writeLong(file.length() / (1024 * 512));

						bytes = new byte[512 * 1024];
						FileInputStream fis = new FileInputStream(file);
						int count;
						while ((count = fis.read(bytes)) != -1) {
							out.write(bytes, 0, count);
							out.flush();
						}
						fis.close();

						out.writeUTF("Okay");

						user = sm.check(Integer.parseInt(qp.get("session")));
						usedSpace = util.folderSize(userPath.toFile());
						changeSpace(user, usedSpace / 1000000.00);
						changeStage(user, false);
						break;
					case operationCode.ALLHASH:
						if (util.isWin())
							userPath = Paths.get(HOME.toString() + "\\" + sm.check(Integer.parseInt(qp.get("session"))));
						else
							userPath = Paths.get(HOME.toString() + "/" + sm.check(Integer.parseInt(qp.get("session"))));
						out.writeUTF(hash.allFiles(userPath));
						out.writeUTF("Okay");

						break;
					case operationCode.DELETE:
						fileName = new String(qp.getContent());
						if (util.isWin()) {
							userPath = Paths.get(HOME.toString() + "\\" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "\\" + fileName);
						} else {
							userPath = Paths.get(HOME.toString() + "/" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "/" + fileName);
						}

						delete(file);
						file.delete();

						user = sm.check(Integer.parseInt(qp.get("session")));
						usedSpace = util.folderSize(userPath.toFile());
						changeSpace(user, usedSpace / 1000000.00);

						text.append(addDate() + user + " is deleting " + file + ".\n");

						out.writeUTF("Okay");

						break;
					case operationCode.SPACE:
						if (util.isWin()) {
							userPath = Paths.get(HOME.toString() + "\\" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "\\");
						} else {
							userPath = Paths.get(HOME.toString() + "/" + sm.check(Integer.parseInt(qp.get("session"))));
							file = new File(userPath + "/");
						}

						String s = String.valueOf(util.folderSize(file));
						out.writeUTF(s);
						out.writeUTF("Okay");
						break;
				}

			} else {
				out.writeUTF("Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	protected void finalize() {
		try {
			super.finalize();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
}
