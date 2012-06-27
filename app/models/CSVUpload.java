package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import play.data.validation.Constraints.*;

public class CSVUpload {
	File file;
	@Required
	@MinLength(5)
	public String urlRedirection;
	public String type;
	public String filepath;
	public List<AdditionalTitle> additionalTitles;
	public String separator;

	public CSVUpload() {}
	
	public CSVUpload(File file, String urlRedirection, String type, AdditionalTitle... additionalTitles) {
        	this.file = file;
	        this.urlRedirection = urlRedirection;
        	this.type = type;
	        this.additionalTitles = new ArrayList<AdditionalTitle>();
        	for(AdditionalTitle additionalTitle: additionalTitles) {
	            this.additionalTitles.add(additionalTitle);
        	}
    }
    

public static class AdditionalTitle{

       public List<Title> titles;
       public AdditionalTitle(){}
		public AdditionalTitle(String... titles){    	
	            
            this.titles = new ArrayList<Title>();
            for(String title: titles) {
                this.titles.add(new Title(title));
            }
        
        
        }
        
        public List<Title> getTitles(){
			return titles;  	
        	}
 }
      
public static class Title {
            
            
            public String title;
            
            public Title() {}
                        
            public Title(String title) {
                this.title = title;
            }
            
            public String getTitle(){
					return title;            	
            	}
            
        }
}
