package Client;

import Utility.operationCode;

import java.io.File;

public class cMain {

	public static void main(String[] args) {
		try {
			System.out.println("Registering....");
			Thread t = (new Thread(new Utility.newSend("127.0.0.1", 4444, operationCode.REGISTER, "cloud|dbtest", "")));
			t.start();
			t.join();
			System.out.println("Logging In");
			t = (new Thread(new Utility.newSend("127.0.0.1", 4444, operationCode.LOGIN, "cloud|dbtest", "")));
			t.start();
			t.join();
			System.out.println("Uploading file");
			(new Thread(new Utility.newSend("127.0.0.1", 4444, operationCode.UPLOAD, new File("/Users/Touch/Desktop/test.png")))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
