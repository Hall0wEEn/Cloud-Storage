package Client;

import Utility.operationCode;

public class cMain {

	public static void main(String[] args) {
		try {
			System.out.println("Setting directory....");
			new send("127.0.0.1", 4444, "/Users/Touch/Cloud Storage/test/");
			System.out.println("Registering....");
			Thread t = (new Thread(new send(operationCode.REGISTER, "test|test")));
			t.start();
			t.join();
			System.out.println("Logging In");
			t = (new Thread(new send(operationCode.LOGIN, "test|test")));
			t.start();
			t.join();
			System.out.println("Uploading file");
//			(new Thread(new send(operationCode.HELO, "", ""))).start();
//			(new Thread(new send(operationCode.SPACE, "", ""))).start();
//			(new Thread(new send(operationCode.DELETE, "c.zip", ""))).start();
//			(new Thread(new send(operationCode.DOWNLOAD, "test.txt", ""))).start();
//			(new Thread(new send(operationCode.UPLOAD, new File("/Users/Touch/Cloud Storage/test/intellij-13-keygen/keygen.class")))).start();
//			(new Thread(new send(operationCode.ALLHASH))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
