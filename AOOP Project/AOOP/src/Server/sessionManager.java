package Server;

import java.util.ArrayList;

class Session {
	String user;
	String token;

	Session(String username) {
		user = username;
		token = String.valueOf(user.hashCode());
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

	public String getSessioin(String user) {
		for (Session s : session)
			if (s.user.equals(user))
				return s.token;
		return null;
	}

	public String check(int token) {
		String tok = String.valueOf(token);
		for (Session s : session) {
			if (s.token.equals(tok))
				return s.user;
		}
		return null;
	}

	public String toString() {
		String output = "";
		for (Session ses : session) {
			output += ses.user + ":" + ses.token + "\n";
		}
		return output;
	}
}
