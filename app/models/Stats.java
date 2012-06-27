package models;

public class Stats {
	private int[] month;
	private int[] year;
	
	public Stats() {
		month = new int[31];
		year = new int[12];
	}
	
	public void addToMonth(int day) {
		if (day >= 0 && day < 32)
			month[day]++;
	}
	
	public void addToYear(int month) {
		if (month >= 0 && month < 13)
			year[month]++;
	}
	
	public String getInfo() {
		return "today : " + month[0] + ", this month : " + year[0];
	}
}