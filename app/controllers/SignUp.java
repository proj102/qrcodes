package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.signup.*;

import models.*;

public class SignUp extends Controller {
    
	/**
	* Defines a form wrapping the User class.
	*/ 
	final static Form<User> signupForm = form(User.class);
  
	/**
	* Display a blank form.
	*/ 
	public static Result blank() {
		return ok(form.render(signupForm, Login.loginForm, InfoDisplay.NONE, null));
	}
  
	/**
	* Display a form pre-filled with an existing account.
	*/
	public static Result edit() {
		User existingUser = new User(
			"fakeuser", "fake@gmail.com", "secret",
			new User.Profile("France", "Durand", "Pierre" , "Télécom ParisTech")
		);
		return ok(form.render(signupForm.fill(existingUser), Login.loginForm, InfoDisplay.NONE, null));
	}
  
	/**
	* Handle the form submission.
	*/
	public static Result submit() {
		Form<User> filledForm = signupForm.bindFromRequest();
		
		// Check accept conditions
		if(!"true".equals(filledForm.field("accept").value())) {
			filledForm.reject("accept", "You must accept the terms and conditions.");
		}
        
		// Check repeated password
		if(!filledForm.field("password").valueOr("").isEmpty()) {
			if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
				filledForm.reject("repeatPassword", "Passwords do not match.");
			}
		}
        
		// Check if the username is valid (not in the data base)
		if(!filledForm.hasErrors()) {
			try {
				MonDataBase.getInstance().addCustomer(filledForm.field("username").value(), filledForm.field("password").value(), filledForm.field("email").value(), filledForm.field("profile.lastName").valueOr(""), filledForm.field("profile.firstName").valueOr(""), filledForm.field("profile.firm").valueOr(""));
			} catch(LoginException e) {
				filledForm.reject("username", "This username is already taken");
				return badRequest(form.render(filledForm, Login.loginForm, InfoDisplay.ERROR, "Subscription failed : this username is already taken."));
			}
			catch (Exception e) {
				return badRequest(form.render(filledForm, Login.loginForm, InfoDisplay.ERROR, "Subscription failed. " + e));
			}
		}

		if(filledForm.hasErrors()) {
			return badRequest(form.render(filledForm, Login.loginForm, InfoDisplay.ERROR, "Subscription failed : please fill correctly all the requieted fields."));
		} else {
			User created = filledForm.get();
			return ok(summary.render(created, Login.loginForm, InfoDisplay.SUCCESS, "Subscription successful."));
		}
	}
}
