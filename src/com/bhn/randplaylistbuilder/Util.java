/*
 * 
 * I'm using this class to resolve an issue i had using Toast
 * My application quickly got populated with more Toast messages than the application could process
 * in a timely manner.
 * 
 * So what i did is just create one toast message for the entire app whose message
 * is updated and shown via popToast(String);
 * 
 */
package com.bhn.randplaylistbuilder;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

public final class Util extends Activity {
	public static Toast toast;
	private static Activity context;
	
	public Util(Activity context) {
		Util.context = context;
		Util.toast = Toast.makeText(Util.context, "", Toast.LENGTH_SHORT);
		Util.toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	public static void popToast(String msg) {
		Util.toast.setText(msg);
		Util.toast.show();
	}
}