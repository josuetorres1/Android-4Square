/**
 * 
 */
package com.josue.square;

/**
 * @author jtorres
 *
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.josue.square.R;

public class splash extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	setContentView(R.layout.splash);
        	
        	Thread splashThread = new Thread() {
                @Override
                public void run() {
                   try {
                      int waited = 0;
                      while (waited < 2000) {
                         sleep(100);
                         waited += 100;
                      }
                   } catch (InterruptedException e) {
                      //Log.e("square", "Charity onCreate " + e);
                   } finally {
                      finish();
                      StartNextActivity();                                            
                   }
                }
             };
             splashThread.start();
        }
        catch (Exception e) {
        	StartNextActivity();
        }
     }
    
    private void StartNextActivity()
    {
    	Intent i = new Intent();
        i.setClassName(getResources().getString(R.string.packagename),
          "com.josue.square.map");
    	startActivity(i);
    }
}