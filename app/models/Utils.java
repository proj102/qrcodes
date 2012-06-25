package models;

import java.lang.Exception;

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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.Formatter;
import java.security.NoSuchAlgorithmException;

public class Utils {

	/**
	*  Parse CSV file and get back a table of JSON documents
	*  for each lines
	*  default => 	separator : ';'
	*		string encapsulation : " "
	*/
	public static ArrayList<BasicDBObject> parseCSV(String file, HashMap<String, String> mapTitles, String separator ) throws IOException{
		BufferedReader buff;
		String line;
		String[] tabLine;
		ArrayList<BasicDBObject> documents = new ArrayList<BasicDBObject>();
		buff = new BufferedReader(new FileReader(file));
		
		while ((line = buff.readLine()).length() == 0);
		// First ligne => Columns titles
		tabLine = getStringTable(line,separator);
		ArrayList<String> titles = getColumnsTitles(tabLine);

		// for each line of data
		while ((line = buff.readLine()) != null){
			if (line.length() != 0)
				tabLine = getStringTable(line,separator);
				documents.add(getJSONDocument(tabLine, titles, mapTitles));
		}
		return documents;
	}

	/**
	*  check if titles in document match titles wrote by user
	*/
	private static void matchColumnsTitles(ArrayList<String> titles, 
	HashMap<String, String> mapTitles){
		//to do
	}

	/**
	*  for one csv line => create a BasicBSONObject
	*  and add it in array list
	*  A BasicBSONObject is a used as JSON document
	*  and send to db as query
	*/
	private static BasicDBObject getJSONDocument(String[] element,
	ArrayList<String> titles, HashMap<String, String> mapTitles){
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
		return b;
	}

	/**
	*  Set columns titles and save in public array title
	*/
	private static ArrayList<String> getColumnsTitles(String[] element){
		ArrayList<String> titles = new ArrayList<String>();
		for (String t : element)
			titles.add(t);
		return titles;
	}

	private static String generateColumnTitle(){
		return UUID.randomUUID().toString();	
	}

	private static String[] getStringTable(String line, String separator){
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
	
	// convert a timestamp to the given format
	public static String dateFromTimestamp(long timestamp, String format) {
		return new SimpleDateFormat(format).format(new Timestamp(timestamp));
	}
}
