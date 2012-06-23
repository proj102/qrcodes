package models;

public class PasswordException extends CustomerException {
	public PasswordException() {
		message = "PasswordException.";
	}
	
	public PasswordException(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}