package models;

// depicts a Qrcode
public class Qrcode {

	private int id;
	private String redirection;
	private String type;
	private String title;
	private String place;
	private long creationDate;
	private int flashs;
	
	
	public Qrcode() {
	}
	
	public Qrcode(int id, String redirection) {
		this.id = id;
		this.redirection = redirection;
	}
	
	public Qrcode(int id, String redirection, String type, String title, String place, long creationDate, int flashs) {
		this.id = id;
		this.redirection = redirection;
		this.type = type;
		this.title = title;
		this.place = place;
		this.creationDate = creationDate;
		this.flashs = flashs;
	}
	
	public int getId() {
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
	
	public long getCreationDate() {
		return creationDate;
	}
	
	public int getFlashs() {
		return flashs;
	}
}

