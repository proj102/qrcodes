package models;

public class Stats {
	private int[] month;
	private int[] year;
	private int[] userAgent;
	
	public Stats() {
		month = new int[31];
		year = new int[12];
		userAgent= new int[5];
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
				this.userAgent[0]++;				
				}
			else if(userAgent.contains("iPhone")){
				this.userAgent[1]++;				
				}
			else if(userAgent.contains("Windows Phone")){
				this.userAgent[2]++;				
				}
			else if(userAgent.contains("BlackBerry")){
				this.userAgent[3]++;				
			}
			else
				this.userAgent[4]++;
		}
	public int[] getMonth(){
			return this.month;		
		}
		
	public int[] getYear(){
			return this.year;		
		}
		
	public int[] getUserAgent(){
			return this.userAgent;		
		}
	
	public String getInfo() {
		return "today : " + month[0] + ", this month : " + year[0]+ " , Android : "+userAgent[0]+ " , iPhone : "+userAgent[1]+ " , Windows Phone : "+userAgent[2]+ " , BlackBerry : "+userAgent[3]+ " , Autre : "+userAgent[4];
	}
}
