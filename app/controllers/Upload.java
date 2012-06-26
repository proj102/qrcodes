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
<<<<<<< HEAD
 	 
        return ok(batchGeneratorCSV.render(uploadForm, Login.loginForm));
=======
        return ok(batchGeneratorCSV.render(uploadForm, Login.loginForm, InfoDisplay.NONE, null));
>>>>>>> 57942df5936cd5d6f63594f6100c074f90391697
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
<<<<<<< HEAD
            return badRequest(batchGeneratorCSV.render(filledForm, Login.loginForm/*, new ArrayList<String>() */));
=======
            return badRequest(batchGeneratorCSV.render(filledForm, Login.loginForm, InfoDisplay.ERROR, "Please fill correctly the fields"));
>>>>>>> 57942df5936cd5d6f63594f6100c074f90391697
        } else {
            CSVUpload created = filledForm.get();
            created.filepath = file.getAbsolutePath();
            return ok(uploadSummary.render(created, Login.loginForm));
        }
  } else {
    flash("error", "Missing file");
    return redirect(routes.Application.index());    
  }
       
        
        
    }
     public static Result addField() {
     	Form<CSVUpload> filledForm = uploadForm.bindFromRequest();
    	
    	if(filledForm.hasErrors()) {
            return badRequest(batchGeneratorCSV.render(filledForm, Login.loginForm/*, new ArrayList<String>() */));
        } else {
            CSVUpload created = filledForm.get();
            ArrayList<String> fieldsTitlesList = created.getAdditionalFieldsTitles();
    			fieldsTitlesList.add("TitleTest");
    			
    			
            return ok(batchGeneratorCSV.render(uploadForm, Login.loginForm/*, fieldsTitlesList*/));
        }
 }
  
}
