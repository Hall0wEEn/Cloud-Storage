package Server;

import Utility.hash;
import Utility.operationCode;
import Utility.queryParser;

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
	Path DIR;

	clientHandler(Socket sock, sessionManager sm, String path) {
		socket = sock;
		this.sm = sm;
		this.DIR = Paths.get(path);
	}

	private void send(String s) throws IOException {
		out.writeUTF(s);
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
						File dir = new File(DIR.toString() + "/" + user);
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

						connection = null;

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
						} else
							out.writeUTF("Incorrect");
						break;
					case operationCode.LOGOUT:
						user = new String(content, "UTF-8");
						sm.destroySession(user);
						break;
					case operationCode.UPLOAD:
						Path userPath = Paths.get(DIR.toString() + "/" + sm.check(Integer.parseInt(qp.get("session"))));
						File file = new File(userPath + "/" + qp.get("fileName"));
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
						bos.write(content);
						bos.flush();

						out.writeUTF("Okay");
						break;
					case operationCode.DOWNLOAD:
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
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
