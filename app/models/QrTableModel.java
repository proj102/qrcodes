package models;

import java.util.*;

public class QrTableModel {

	//private ArrayList<String> check_qr;
	private String item;
	private String[] item2;
	
	public QrTableModel() {
		//check_qr = new ArrayList<String>();
	}
	
	public QrTableModel(String item, String[] item2) {
		//this.check_qr = check_qr;
		this.item = item;
		this.item2 = item2;
	}
	
	public String getInfo() {
		return "taille : " + item;
	}
	
}