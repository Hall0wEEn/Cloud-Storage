package Client;

import Utility.operationCode;

public class cMain {

	public static void main(String[] args) {
		try {
			System.out.println("Setting directory....");
			new send("127.0.0.1", 4444, "/Users/Touch/Desktop/cloud/client/");
			System.out.println("Registering....");
			Thread t = (new Thread(new send(operationCode.REGISTER, "server|server", "")));
			t.start();
			t.join();
			System.out.println("Logging In");
			t = (new Thread(new send(operationCode.LOGIN, "server|server", "")));
			t.start();
			t.join();
			System.out.println("Uploading file");
			(new Thread(new send(operationCode.HELO, "", ""))).start();
//			(new Thread(new send(operationCode.SPACE, "", ""))).start();
//			(new Thread(new send(operationCode.DELETE, "c.zip", ""))).start();
//			(new Thread(new send(operationCode.DOWNLOAD, "test.txt", ""))).start();
//			(new Thread(new send(operationCode.UPLOAD, new File("/Users/Touch/Desktop/cloud/client/test.pdf")))).start();
//			(new Thread(new send(operationCode.ALLHASH))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
