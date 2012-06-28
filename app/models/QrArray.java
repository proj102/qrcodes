package models;

import java.util.ArrayList;
import controllers.QrTable;

public class QrArray extends ArrayList<Qrcode> {

	private int totalNumber;
	private int current;
	
	public QrArray() {
		totalNumber = 0;
		current = 0;
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
	
	// get the current qr that is processed
	public int getCurrent() {
		return ++current;
	}
	
	public int getNumber() {
		return totalNumber;
	}
}
