package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.upload.*;
import models.*;

import java.io.File;
import play.mvc.Http.MultipartFormData.*;
import play.mvc.Http.MultipartFormData;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import models.*;

public class Upload extends Controller {
    
    /**
     * Defines a form wrapping the CSVUpload class.
     */ 
    final static Form<CSVUpload> uploadForm = form(CSVUpload.class);
  
    /**
     * Display a blank form.
     */ 
    public static Result blank() {
        //return ok(batchGeneratorCSV.render(uploadForm, Login.loginForm));
        return ok(batchGeneratorCSV.render(Login.getCustSession(), uploadForm, InfoDisplay.NONE, null));
    }
  
    
  
    /**
     * Handle the form submission.
     */
	public static Result submit() {
		Form<CSVUpload> filledForm = uploadForm.bindFromRequest();

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart picture = body.getFile("picture");
		if (picture != null) {
			String fileName = picture.getFilename();
			String contentType = picture.getContentType(); 
			File file = picture.getFile();
			File dir = new File("./tmp");
			file.renameTo(new File(dir, file.getName()));
	
			if(filledForm.hasErrors()) {
			    return badRequest(batchGeneratorCSV.render(filledForm, Login.loginForm, InfoDisplay.ERROR, "Please fill correctly the fields"));
			} else {
				CSVUpload created = filledForm.get();
			 	created.filepath = file.getAbsolutePath();
				try {
					parseAndLoad(created.filepath);
					return ok(uploadSummary.render(created, Login.loginForm));
				}catch(Exception e){
					return badRequest("Problème dans upload!! " + created.filepath + "\n Erreur: " + e.getMessage());
				}
			}
		} else {
		flash("error", "Missing file");
		return redirect(routes.Application.index());   
		}
	}

	public static void parseAndLoad(String file) throws Exception{
		MonDataBase db = MonDataBase.getInstance();
		ArrayList<BasicDBObject> json = new ArrayList<BasicDBObject>();
		// get array of json document from csv file
		json = Utils.parseCSV(file, "redirection",";");
		// insertion in db only if document doesn't exist in collection
		for (BasicDBObject j : json){
			if ( !db.isCreated("Société", j.get("Société"), "qrcodes") )
				db.insertQr(j);
		}
	}
}
