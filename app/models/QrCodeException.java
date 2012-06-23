package models;

import java.lang.Exception;

public class QrCodeException extends Exception {
	private String message;
	
	public QrCodeException() {
		message = "QrCodeException.";
	}
	
	public QrCodeException(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}