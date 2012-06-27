package controllers;

import java.util.*;

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

			hmap.put("id",filledForm.field("id").value());

			// Insert the login field if not empty
			if(!filledForm.field("login").value().isEmpty()) {
				hmap.put("login", filledForm.field("login").value());
			}

			// Insert the url field if not empty
			if(!filledForm.field("urlToAvatar").value().isEmpty()) {
				hmap.put("avatar", filledForm.field("urlToAvatar").value());
			}

			MongoDB.updateCustomer(hmap);
			CustomerSession customer = Login.getCustSession();
			return ok(edit.render(Login.getCustSession(), customForm.fill(customer), InfoDisplay.SUCCESS, "Your changes have been updated."));
		} else {
			CustomerSession customer = Login.getCustSession();
			return ok(edit.render(Login.getCustSession(), customForm.fill(customer), InfoDisplay.ERROR, "Please fill at least one field."));			
		}
	}

}
