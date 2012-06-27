package models;

import java.util.ArrayList;
import controllers.QrTable;

public class QrArray extends ArrayList<Qrcode> {

	private int totalNumber;
	
	public QrArray() {
		totalNumber = 0;
	}
	
	public void setNumber(int number) {
		totalNumber = number;
	}
	
	public ArrayList<Integer> getPages() {
		int numberOfPages = (int) Math.ceil(((float) totalNumber) / ((float) QrTable.qrPerPage));
		
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 0 ; i < numberOfPages ; i++) {
			ret.add(i+1);
		}
		return ret;
	}
	
	public int getNumber() {
		return totalNumber;
	}
}
