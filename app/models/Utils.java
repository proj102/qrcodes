package models;

import java.lang.Exception;

//import org.bson.BasicBSONObject;
import com.mongodb.BasicDBObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.Formatter;
import java.security.NoSuchAlgorithmException;

public class Utils {

	// list ordered of columns titles in the document (first line)
	public ArrayList<String> titles; 
	// map from the ihm form : db name => document columns title 
	public HashMap<String, String> mapTitles;

	private String separator;

	public Utils(){
		this.titles = new ArrayList<String>();
		this.separator = ";";
	}

	public Utils(HashMap<String, String> mapTitles) {
		this.titles = new ArrayList<String>();
                this.separator = ";";
		this.mapTitles = mapTitles;
	}

	// Parse CSV file and get back a table of JSON documents
	// for each lines
	// default => 	separator : ';'
	//		string encapsulation : " "
	public ArrayList<BasicDBObject> parseCSV(String file) throws IOException{
		BufferedReader buff;
		String line;
		ArrayList<BasicDBObject> documents = new ArrayList<BasicDBObject>();
		buff = new BufferedReader(new FileReader(file));
		
		while ((line = buff.readLine()).length() == 0);
		setColumnsTitles(line); // First ligne => Columns titles

		// for each line of data
		while ((line = buff.readLine()) != null){
			if (line.length() != 0)
				setDocument(documents, line);
		}
		return documents;
	}

	public ArrayList<BasicDBObject> parseCSV(String file, String separator) throws IOException{
		this.separator = separator;
		return parseCSV(file);
	}

	public void setMapTitles(HashMap<String, String> mapTitles){
		this.mapTitles = mapTitles;
	}

	// check if titles in document match titles wrote by user
	public void matchColumnsTitles(ArrayList<String> titles, HashMap<String, String> mapTitles){
		//to do
	}

	// for one csv line => create a BasicBSONObject
	// and add it in array list
	// A BasicBSONObject is a used as JSON document
	// and send to db as query
	public void setDocument(ArrayList<BasicDBObject> list, String line){
		String[] element = getStringTable(line);
		BasicDBObject b = new BasicDBObject();
		int i = 0;
		// set the JSON document 
		for (String title : titles){
			//match betwin title in doc and title enter by the user
			if (mapTitles.get(title) == null) // if no title for coll
				b.put(generateColumnTitle(), element[i]);
			else 
				b.put(mapTitles.get(title), element[i]);
			i++;
		}
		list.add(b);
	}

	// Set columns titles and save in public array title
	public void setColumnsTitles(String line){
		String[] element = getStringTable(line);
		for (String t : element)
			titles.add(t);
	}

	public String generateColumnTitle(){
		return UUID.randomUUID().toString();	
	}

	public String[] getStringTable(String line){
		String s = StringUtils.remove(line, "\"");
		return StringUtils.split(s, separator);
	}
	
	// encrypt the given string in SHA-1
	public static String sha1Encrypt(String s) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] hash = md.digest(s.getBytes());
		
		Formatter formatter = new Formatter();
		for (byte b : hash)
			formatter.format("%02x", b);
		
		return formatter.toString();
	}
}
