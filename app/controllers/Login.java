package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import views.html.signup.*;

import models.*;

import play.mvc.Http.Request;
import play.mvc.Http.Cookie;

public class Login extends Controller {
    
	public static int sessionDuration = 3600*24*30;
	
	static Form<Client> loginForm = form(Client.class);
	
	public static String connection(String login, String password) throws Exception {

		MonDataBase db = MonDataBase.getInstance();
		
		String customerId = db.connection(login, password);
			
		if (customerId != null)
			response().setCookie("connected", customerId, sessionDuration);
			
		return customerId;
	}

	// test method
	public static Result isConnected() throws Exception{
		if(getConnected()!=null)	
			return ok("connect");
		else
			return ok("no");
	}
	
	public static String getConnected() throws Exception {
		MonDataBase db = MonDataBase.getInstance();
		Http.Cookie cookie = request().cookies().get("connected");
		
		if (cookie != null) {
			String custId = cookie.value();
			
			return db.custExists(custId);
		}
			
		return null;
	}
	
	public static Result logout() {
		response().discardCookies("connected");
		
		return ok(index.render(Application.urlForm, Login.loginForm, InfoDisplay.INFO, "You are now logged out."));
	}

	public static Result blank() {
		return ok(login.render(loginForm));
	}

	public static Result submit() {
		Form<Client> filledForm = loginForm.bindFromRequest();
		
		//Check in the database if login exist and if it matches with the password
		if(!filledForm.hasErrors()) {
			try {
				String checker = connection(filledForm.field("login").value(), filledForm.field("password").value());
				
				Client created = filledForm.get();
				return ok(index.render(Application.urlForm, filledForm, InfoDisplay.SUCCESS, "You are now connected."));					
			}
			catch (LoginException e) {
				filledForm.reject("login", "This login does not exist");
				return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. This login does not exist."));
			}
			catch (PasswordException e) {
				filledForm.reject("password", "The password does not match");
				return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. The password does not match."));
			}
			catch(Exception e) {
				return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. " + e));
			}
		}
		
		return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. Please fill correctly all the fields."));
	}
}



