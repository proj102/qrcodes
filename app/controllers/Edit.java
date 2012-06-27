package controllers;

import java.util.*;
import java.util.regex.Pattern;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;

import models.*;

public class Edit extends Controller {

	static Form<CustomerSession> customForm = form(CustomerSession.class);

	public static Result edit() {
		CustomerSession custSession = Login.getCustSession();

		return ok(edit.render(custSession, customForm.fill(custSession), InfoDisplay.NONE, null));
	}

	public static Result submit() {

		Form<CustomerSession> filledForm = customForm.bindFromRequest();
		MonDataBase MongoDB = MonDataBase.getInstance();
		HashMap<String, String> hmap = new HashMap<String, String>();

		if(!filledForm.field("login").value().isEmpty() || !filledForm.field("urlToAvatar").value().isEmpty()){
			
			//To check if the url to avatar is correct (http and extension png, gif or jpg)
			String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|].(?:jpg|gif|png)$";
			if(Pattern.matches(regex, filledForm.field("urlToAvatar").value())){

				hmap.put("id",filledForm.field("id").value());

				// Insert the login field if not empty
				if(!filledForm.field("login").value().isEmpty()) {
					hmap.put("login", filledForm.field("login").value());
				}

				// Insert the url field if not empty and if it's a correct url form, otherwise the default avatar is selected 
				
				if(!filledForm.field("urlToAvatar").value().isEmpty()) {
					hmap.put("avatar", filledForm.field("urlToAvatar").value());
				} else {
					hmap.put("avatar", "default");
				}

				MongoDB.updateCustomer(hmap);
				CustomerSession customer = Login.getCustSession();
				return ok(edit.render(customer, customForm.fill(customer), InfoDisplay.SUCCESS, "Your changes have been updated."));
			} else {
				CustomerSession customer = Login.getCustSession();
				return ok(edit.render(customer, customForm.fill(customer), InfoDisplay.ERROR, "You must enter a correct url, check if it's correct and if it's a PNG, JPG or GIF image"));			
			}
		} else {
			CustomerSession customer = Login.getCustSession();
			return ok(edit.render(customer, customForm.fill(customer), InfoDisplay.ERROR, "Please fill at least one field."));
		}
	}

}
