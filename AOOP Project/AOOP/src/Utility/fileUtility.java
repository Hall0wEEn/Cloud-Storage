package Utility;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Touch
 * Date: 11/16/13
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class fileUtility {
	public static void writeFile(String s, byte[] bytes) throws IOException {
		File f = new File(s);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		bos.write(bytes);
		bos.flush();
		bos.close();
	}

	public static byte[] readFile(String s) throws Exception {
		File f = new File(s);
		byte[] bytes = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(bytes);
		fis.close();
		return bytes;
	}
}
