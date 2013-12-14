package Utility;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Formatter;

public class hash {
	public static String sha1(final File file) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

		if (!file.exists())
			return null;

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[1024];
			for (int read; (read = is.read(buffer)) != -1; ) {
				messageDigest.update(buffer, 0, read);
			}

			Formatter formatter = new Formatter();
			for (byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static String sha1(String input) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

		try {
			InputStream is = new ByteArrayInputStream(input.getBytes());
			byte[] buffer = new byte[1024];
			for (int read; (read = is.read(buffer)) != -1; ) {
				messageDigest.update(buffer, 0, read);
			}

			Formatter formatter = new Formatter();
			for (byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static String sha1(byte[] bytes) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		Formatter formatter = new Formatter();
		for (byte b : messageDigest.digest(bytes))
			formatter.format("%02x", b);
		return formatter.toString();
	}

	public static String sha1(byte[] bytes, int start, int end) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		Formatter formatter = new Formatter();
		byte[] tmpBytes = new byte[end - start];
		System.arraycopy(bytes, start, tmpBytes, start, end - start);
		for (byte b : messageDigest.digest(tmpBytes))
			formatter.format("%02x", b);
		return formatter.toString();
	}

	public static String allFiles(Path path) {
		File root = new File(path.toString());
		int rootLen = root.toString().length() + 1;
		return allFiles(root, rootLen);
	}

	private static String allFiles(File file, int rootLen) {
		String output = "";
		if (file.isDirectory()) {
			for (File tmpFile : file.listFiles())
				output += allFiles(tmpFile, rootLen);
		} else {
			try {
				if (util.getExt(file).equals("part") || file.getName().charAt(0) == '.')
					return "";
				return sha1(file) + " " + file.lastModified() + " " + file.getAbsolutePath().substring(rootLen) + "\n";
			} catch (Exception e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
		return output;
	}

	public static void main(String[] args) {
		System.out.println(allFiles(Paths.get("/Users/Touch/Desktop/cloud")));
	}
}
