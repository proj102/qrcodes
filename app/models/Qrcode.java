package models;

import controllers.*;
import java.util.ArrayList;

// depicts a Qrcode
public class Qrcode {

	private String id;
	private String redirection;
	private String type;
	private String title;
	private String place;
	private long creationDate;
	private int flashs;
	private ArrayList<String> additionalFieldsTitles = new ArrayList<String>();
	private ArrayList<String> additionalFieldsValues = new ArrayList<String>();
	
	
	public Qrcode() {
	}
	
	public Qrcode(String id, String redirection) {
		this.id = id;
		this.redirection = redirection;
	}
	
	public Qrcode(String id, String redirection, String type, String title, String place, long creationDate, int flashs) {
		this.id = id;
		this.redirection = redirection;
		this.type = type;
		this.title = title;
		this.place = place;
		this.creationDate = creationDate;
		this.flashs = flashs;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRedirection() {
		return redirection;
	}
	
	public String getType() {
		return type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getPlace() {
		return place;
	}
	
	public String getCreationDate() {
		return Utils.dateFromTimestamp(creationDate, "dd/MM/yyyy");
	}
	
	public String getUrl() {
		return Application.domain + "r/" + id;
	}
	
	public int getFlashs() {
		return flashs;
	}
	
	public ArrayList<String> getAdditionalFieldsTitles() {
		return additionalFieldsTitles;
	}
	public ArrayList<String> getAdditionalFieldsValues() {
		return additionalFieldsValues;
	}
}

