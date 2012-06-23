package models;

public class LoginException extends CustomerException {
	public LoginException() {
		message = "LoginException.";
	}
	
	public LoginException(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}