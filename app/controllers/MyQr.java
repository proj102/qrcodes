package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import views.html.*;
import models.*;

import play.data.*;
import play.data.validation.Constraints.*;
import java.util.*;
import java.io.File;
import play.mvc.Http.MultipartFormData.*;
import play.mvc.Http.MultipartFormData;


public class MyQr extends Controller {

	static Form<Qrcode> qrForm = form(Qrcode.class);
	
	public static Result viewQr(int id) {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			Qrcode qr = db.getQrCode(id);
			
			return ok(myQr.render(qr, Login.loginForm, InfoDisplay.NONE, null));
		}
		catch (Exception e) {
			try {
				return ok(myQrTable.render(Login.loginForm, db.getCustomersQrs(), InfoDisplay.ERROR, "Cannot view this QrCode. " + e));
			}
			catch (Exception f) {
				return badRequest(myQrTable.render(Login.loginForm, new ArrayList<Qrcode>(), InfoDisplay.ERROR, "Impossible to get your Qrcodes." + f));
			}
		}
	}
	
	public static Result submit() {

/**
  *TODO: Implement the MonDataBase method setQrcode
 **/		
/*		Form<Qrcode> filledForm = qrForm.bindFromRequest();
		MonDataBase MongoDB = MonDataBase.getInstance();
		Qrcode qr = MongoDB.getQrCode(filledForm.field("id").value());
		
		// Insert the redirection field if not empty
		if(!filledForm.field("redirection").isEmpty()) {
			MongoDB.setQrcode(filledForm.field("id").value(),filledForm.field("redirection").value(),"","","","","")
		}

		// Insert the title field if not empty
		if(!filledForm.field("title").isEmpty()) {
			MongoDB.setQrcode(filledForm.field("id").value(),"","",filledForm.field("title").value(),"","","")
		}

		// Insert the place field if not empty
		if(!filledForm.field("place").isEmpty()) {
			MongoDB.setQrcode(filledForm.field("id").value(),"","","",filledForm.field("place").value(),"","")
		}

		if(!filledForm.field("redirection").isEmpty() || !filledForm.field("title").isEmpty() || !filledForm.field("place").isEmpty(){
			return ok(myQr.render(qr, Login.loginForm, InfoDisplay.SUCCESS, "Your changes have been uploaded"));
		}else{
			return ok(myQr.render(qr, Login.loginForm, InfoDisplay.ERROR, "No changes detected"));
		}
		
	}	*/	
		return badRequest("To be implemented.");
	}
	
}


