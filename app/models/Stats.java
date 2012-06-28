package models;

public class Stats {
	private int[] month;
	private int[] year;
	private int[] userAgent;
	
	public Stats() {
		month = new int[31];
		year = new int[12];
		userAgent= new int[4];
	}
	
	public void addToMonth(int day) {
		if (day >= 0 && day < 32)
			month[day]++;
	}
	
	public void addToYear(int month) {
		if (month >= 0 && month < 13)
			year[month]++;
	}
	
	public void addToUserAgent(String userAgent){
			if (userAgent.contains("Android")){
				userAgent[0]++;				
				}
			else if(userAgent.contains("iPhone")){
				userAgent[1]++;				
				}
			else if(userAgent.contains("Windows Phone")){
				userAgent[2]++;				
				}
			else
				userAgent[3]++;
		}
	
	public String getInfo() {
		return "today : " + month[0] + ", this month : " + year[0]+ " , Android : "+userAgent[0];
	}
}