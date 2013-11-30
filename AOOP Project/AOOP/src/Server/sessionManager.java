package Server;

import java.util.ArrayList;

class Session {
	String user;
	String token;

	Session(String username) {
		user = username;
		token = String.valueOf(username);
	}
}

public class sessionManager {
	ArrayList<Session> session = new ArrayList<Session>();

	public void createSession(String user) {
		session.add(new Session(user));
	}

	public void destroySession(String user) {
		session.remove(new Session(user));
	}

	public String check(String token) {
		for (Session s : session) {
			if (s.token.equals(token))
				return s.user;
		}
		return null;
	}
}
