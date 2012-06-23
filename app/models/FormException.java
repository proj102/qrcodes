package models;

import java.lang.Exception;

public class FormException extends Exception {
	private String message;
	
	public FormException() {
		message = "FormException.";
	}
	
	public FormException(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}