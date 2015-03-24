/**
 * 
 */
package com.josue.square;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jtorres
 *
 */
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler{
	private Activity currentContext;
	  private PendingIntent pendingIntent;
	  
	  //constructor
	  public DefaultExceptionHandler(Activity act, PendingIntent intent)
	  {
		  currentContext = act;
		  pendingIntent = intent;
	  }
	  
	  public void uncaughtException(Thread t, Throwable e) {      
			//Log.e("Artez charity", "DefaultExceptionHandler " + e);
			 
			SharedPreferences settings = currentContext.getSharedPreferences("UncaughtException", currentContext.MODE_PRIVATE);
	 	  	SharedPreferences.Editor prefEditor = settings.edit();  
	   		prefEditor.putString("Exception", e.toString()); 
	   		prefEditor.commit(); 
	   		
	   		AlarmManager mgr = (AlarmManager) currentContext.getSystemService(Context.ALARM_SERVICE);
	   		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pendingIntent);
	   		System.exit(2);

	 }
}
