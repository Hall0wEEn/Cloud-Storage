package Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class fileBlock {
	public static void hashFile(File file) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[4194304]; // 4mb = 4*1024*1024 = 4194304
			fis.read(bytes);
			fis.close();
			FileOutputStream output = new FileOutputStream(new File("/Users/Sinderella/Desktop/test_img.jpg"));
			output.write(bytes);
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			hashFile(new File("/Users/Sinderella/Desktop/test"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
