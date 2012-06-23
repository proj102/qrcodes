package models;

import java.lang.Exception;

public class CustomerException extends Exception {
	protected String message;
	
	public CustomerException() {
		message = "CustomerException.";
	}
	
	public CustomerException(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}