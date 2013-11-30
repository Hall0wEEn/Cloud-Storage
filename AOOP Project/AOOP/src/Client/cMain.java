package Client;

import Utility.operationCode;

public class cMain {

	public static void main(String[] args) {
		try {
			(new Thread(new Utility.send("127.0.0.1", 4444, operationCode.REGISTER, "touch|touch"))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
