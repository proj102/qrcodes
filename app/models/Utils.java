package models;

import java.lang.Exception;

import org.bson.BasicBSONObject;
import com.mongodb.BasicDBObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import play.libs.Json;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParser;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	// list ordered of columns titles in the document (first line)
	public ArrayList<String> titles; 
	// map from the ihm form : db name => document columns title 
	public HashMap<String, String> mapTitles;

	public Utils(){
		titles = new ArrayList<String>();
		mapTitles = new HashMap<String, String>();
	}

	// check if titles in document match titles wrote by user
	public void matchColumnsTitles(ArrayList<String> titles, HashMap<String, String> mapTitles){
		//to do
	}

	public void setDocument(ArrayList<BasicBSONObject> list, String line){
		String s = StringUtils.remove(line, "\"");
		String[] element = StringUtils.split(s, ";");
		BasicBSONObject b = new BasicBSONObject();
		int i = 0;
		for (String title : titles){
			b.put(mapTitles.get(title), element[i]);
			i++;
		}
		list.add(b);
	}

	public ArrayList<BasicBSONObject> parseCSV(String file) throws IOException{
		BufferedReader buff;
		String line;
		ArrayList<BasicBSONObject> documents = new ArrayList<BasicBSONObject>();
		buff = new BufferedReader(new FileReader(file)) ;
		// First ligne => Columns titles
		if ((line = buff.readLine())!= null)
			setColumnsTitles(line);
		while ((line = buff.readLine()) != null)
			setDocument(documents, line);
		return documents;
	}

	public void setColumnsTitles(String line){
                String s = StringUtils.remove(line, "\"");
                String[] element = StringUtils.split(s, ";");
		for (String t : element){
			titles.add(t);
		}
		
		mapTitles.put("Société", "societe"); 
		mapTitles.put("Secteur", "secteur"); 
		mapTitles.put("Poids indiciel", "poids"); 
		mapTitles.put("Rang mondial", "rang");
		mapTitles.put("Chiffre d'affaires", "ca");
		mapTitles.put("Capitalisation boursière", "capital");
		mapTitles.put("redirection", "redirection");
		mapTitles.put("Résultat net", "net");
	}
}
