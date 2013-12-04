package Client;

import Utility.operationCode;

public class cMain {

	public static void main(String[] args) {
		try {
			System.out.println("Setting directory....");
			new send("C:\\Users\\Touch\\Desktop\\cloud\\client");
			System.out.println("Registering....");
			Thread t = (new Thread(new send("127.0.0.1", 4444, operationCode.REGISTER, "server|server", "")));
			t.start();
			t.join();
			System.out.println("Logging In");
			t = (new Thread(new send("127.0.0.1", 4444, operationCode.LOGIN, "server|server", "")));
			t.start();
			t.join();
			System.out.println("Uploading file");
			(new Thread(new send("127.0.0.1", 4444, operationCode.ALLHASH))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
