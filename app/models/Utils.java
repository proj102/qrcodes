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
	public static ArrayList<BasicDBObject> parseCSV(String file, String redirection, String separator ) throws Exception{
		BufferedReader buff;
		String line;
		String[] tabLine;
		ArrayList<BasicDBObject> documents = new ArrayList<BasicDBObject>();
		buff = new BufferedReader(new FileReader(file));
		
		while ((line = buff.readLine()).length() == 0);
		// First ligne => Columns titles
		tabLine = getStringTable(line,separator);
		ArrayList<String> titles = getColumnsTitles(tabLine, redirection);
		// if no title match with the redirection field give by user
		if(!checkRedirection(titles))throw new Exception("No field name '" + redirection + "' in the file" );
		// for each line of data
		while ((line = buff.readLine()) != null){
			if (line.length() != 0){
				tabLine = getStringTable(line,separator);
				documents.add(getJSONDocument(tabLine, titles));
			}
		}
		return documents;
	}

	private static boolean checkRedirection(ArrayList<String> titles){
		for (String t : titles){
			if (t.equals("redirection")) return true;
		}
		return false;
	}

	/**
	*  for one csv line => create a BasicBSONObject
	*  and add it in array list
	*  A BasicBSONObject is a used as JSON document
	*  and send to db as query
	*/
	private static BasicDBObject getJSONDocument(String[] element,
	ArrayList<String> titles){
		BasicDBObject b = new BasicDBObject();
		// set the JSON document 
		for (int i=0; i<titles.size(); i++){
			b.put(titles.get(i), element[i]);
			//b.put(mapTitles.get(title), element[i]);
		}
		return b;
	}

	/**
	*  Set columns titles and save in public array title
	*/
	private static ArrayList<String> getColumnsTitles(String[] element, String redirection) throws Exception{
		ArrayList<String> titles = new ArrayList<String>();
		int i = 0;
		for (String t : element){
			if (t == "")
				titles.add("unknow"+i);
			else if (t.equals(redirection)) // redirection = value wrote by the user
				titles.add("redirection");
			else if (t.equals("redirection"))
				throw new Exception("You can't use the name 'redirection' if it's not for the redirection url");
			else
				titles.add(t);
		}
		return titles;
	}

	// not using function
	private static String format_text(String text){
		StringUtils.lowerCase(text);
		StringUtils.replaceChars("société", "é", "e");
		return "";
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
