package models;

public class CustomerSession {

	private String login;
	private String id;
	boolean justLogged = false;
	
	public CustomerSession(String id, String login) {
		this.login = login;
		this.id = id;
		justLogged = true;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setJustLogged(boolean b) {
		justLogged = b;
	}
	
	public boolean getJustLogged() {
		return justLogged;
	}
}