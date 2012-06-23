package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;

import models.*;

public class QrTable extends Controller {
    
    /**
     * Defines a form wrapping the User class.
     */ 
   // final static Form<User> signupForm = form(User.class);
  
    /**
     * Display a blank form.
     */ 
    public static Result blank() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			return ok(myQrTable.render(Login.loginForm, db.getCustomersQrs()));
		}
		catch (Exception e) {
			return badRequest("Impossible to get the Qrcodes : " + e);
		}
    }
  
    /**
     * Display a form pre-filled with an existing account.
     */
    /*public static Result edit() {
        User existingUser = new User(
            "fakeuser", "fake@gmail.com", "secret",
            new User.Profile("France", "Durand", "Pierre" , "Telecom ParisTech")
        );
        return ok(form.render(signupForm.fill(existingUser), Login.loginForm));
    }*/
  
    /**
     * Handle the form submission.
     */
    public static Result submit() {
        /*Form<User> filledForm = signupForm.bindFromRequest();
        
        // Check accept conditions
        if(!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }
        
        // Check repeated password
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Password don't match");
            }
        }
        
        // Check if the username is valid (not in the data base)
	 if(!filledForm.hasErrors()) {
            try {
		MonDataBase.getInstance().addCustomer(filledForm.field("username").value(), filledForm.field("password").value(), filledForm.field("email").value(), filledForm.field("profile.lastName").valueOr(""), filledForm.field("profile.firstName").valueOr(""), filledForm.field("profile.firm").valueOr(""));
	    } catch(Exception e) {
	        filledForm.reject("username", "This username is already taken");
            }        
	  }

        
        if(filledForm.hasErrors()) {
            return badRequest(form.render(filledForm, Login.loginForm));
        } else {
            User created = filledForm.get();
            return ok(summary.render(created, Login.loginForm));
        }*/
		
		return badRequest("To be implemented ...");
    }
  
}