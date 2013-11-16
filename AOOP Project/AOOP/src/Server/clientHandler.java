package Server;

import Utility.hash;

import java.io.*;
import java.net.Socket;

public class clientHandler implements Runnable {
	Socket socket;

	clientHandler(Socket sock) {
		socket = sock;
	}

	@Override
	public void run() {
		String recvHash;
		byte[] content;
		DataInputStream in = null;
		DataOutputStream out = null;

		try {
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
			content = new byte[byteLength - 40];
			System.arraycopy(bytes, 40, content, 0, content.length);
			System.out.println(recvHash);
			System.out.println(hash.sha1(content));
			if (hash.sha1(content).equals(recvHash)) {
				out.writeUTF("Okay");
//				System.out.println(recv);
				File f = new File("C:\\Users\\Touch\\Desktop\\test2.rar");
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
				bos.write(content);
				bos.flush();
				bos.close();
			} else {
				out.writeUTF("Error");
				System.out.println("Okay");
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
