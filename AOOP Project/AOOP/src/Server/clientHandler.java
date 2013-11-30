package Server;

import Utility.hash;
import Utility.operationCode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;

public class clientHandler implements Runnable {
	Socket socket;
	DataInputStream in = null;
	DataOutputStream out = null;

	clientHandler(Socket sock) {
		socket = sock;
	}

	private String recv() throws Exception {
		String data;
		String recvhash;
		String content;

		while (true) {
			int byteLength = in.readInt();
			byte[] bytes = new byte[byteLength];
			in.readFully(bytes);

			data = new String(bytes, "UTF-8");
			recvhash = data.substring(0, 40);
			content = data.substring(40, data.length());
			if (recvhash.equals(hash.sha1(content))) {
				break;
			}
			out.writeUTF("Error");
		}
		return content;
	}

	private void send(String s) throws IOException {
		out.writeUTF(s);
	}

	@Override
	public void run() {
		String data;
		String tmp;
		String recvHash;
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
			recvHash = (new String(bytes, "UTF-8")).substring(0, 40);
			content = new byte[byteLength - 41];
			System.arraycopy(bytes, 41, content, 0, content.length);
			char oc = (char) bytes[40];
//			System.out.println(recvHash);
//			System.out.println(hash.sha1(content));
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

						connection = null;

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

						if (passwd.equals(retpassword))
							out.writeUTF("Okay");
						else
							out.writeUTF("Incorrect password");
						break;
					case operationCode.LOGOUT:
						break;
					case operationCode.UPLOAD:
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
