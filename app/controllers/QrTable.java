package controllers;

import java.lang.Exception;
import java.util.*;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;

import models.*;

public class QrTable extends Controller {
    
	public static final int qrPerPage = 25;
	final static Form<QrTableModel> deleteForm = form(QrTableModel.class);
  
	// visualisation of the Qrcodes of the customer
    public static Result blank() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			return ok(myQrTable.render(Login.getCustSession(), deleteForm, db.getCustomersQrs(0), InfoDisplay.NONE, null));
		}
		catch (Exception e) {
			return badRequest(myQrTable.render(Login.getCustSession(), deleteForm, null, InfoDisplay.ERROR, "Impossible to get your Qrcodes." + e));
		}
    }
	
	public static Result viewPage(int page) {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			return ok(myQrTable.render(Login.getCustSession(), deleteForm, db.getCustomersQrs(page), InfoDisplay.NONE, null));
		}
		catch (Exception e) {
			return badRequest(myQrTable.render(Login.getCustSession(), deleteForm, null, InfoDisplay.ERROR, "Impossible to get your Qrcodes." + e));
		}
		
	}

    public static Result submit() {
		Form<QrTableModel> filledForm = deleteForm.bindFromRequest();
		
		ArrayList<String> qrIds = new ArrayList<String>();
		
		for (int i = 0 ; i < qrPerPage ; i++) {
			if (filledForm.field("item[" + i + "]").value() != null) {	
				qrIds.add(filledForm.field("item[" + i + "]").value());
			}
		}
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			db.removeQrCode(qrIds);
			
			try {
				return ok(myQrTable.render(Login.getCustSession(), deleteForm, db.getCustomersQrs(0), InfoDisplay.SUCCESS, "The QrCodes you selected have successfully been removed."));
			}
			catch (Exception e) {
				return badRequest(myQrTable.render(Login.getCustSession(), deleteForm, null, InfoDisplay.ERROR, "Impossible to get your Qrcodes." + e));
			}
		}
		catch (Exception e) {
			return badRequest(myQrTable.render(Login.getCustSession(), deleteForm, null, InfoDisplay.ERROR, "Impossible to remove your Qrcodes." + e));
		}
    }
  
}
