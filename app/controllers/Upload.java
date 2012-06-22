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
        return ok(batchGeneratorCSV.render(uploadForm));
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
            return badRequest(batchGeneratorCSV.render(filledForm));
        } else {
            CSVUpload created = filledForm.get();
            created.filepath = file.getAbsolutePath();
            return ok(uploadSummary.render(created));
        }
  } else {
    flash("error", "Missing file");
    return redirect(routes.Application.index());    
  }
       
        
        
    }
  
}
