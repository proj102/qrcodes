package models;

import java.io.File;

public class CSVUpload {
	File file;
	public String urlRedirection;
	public String type;
	public String filepath;
	
	public CSVUpload() {}
	
	public CSVUpload(File file, String urlRedirection, String type) {
        this.file = file;
        this.urlRedirection = urlRedirection;
        this.type = type;
    }
    
    
	
}