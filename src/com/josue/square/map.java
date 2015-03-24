/**
 * 
 */
package com.josue.square;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.josue.square.RequestListener;
import com.josue.square.R;
import com.josue.square.VenuesCriteria;

import android.support.v4.app.FragmentActivity;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
//import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class map extends FragmentActivity {

	GoogleMap map;
	private ArrayList<Venue> arrayVenue;
	Location location;
				
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Set global UncaughtExceptionHandler
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0,
                new Intent(getIntent()), getIntent().getFlags());
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this, intent));
        
    	// Check for exceptions
    	SharedPreferences settings = getSharedPreferences("UncaughtException", MODE_PRIVATE);
    	if(settings.contains("Exception"))
		{
    		HandleException();
		}
        
        MapsInitializer.initialize(this);
        
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map1)).getMap();
        
                
//        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map1);

        //map = mapFrag.getMap();
        
        android.widget.Button go = (android.widget.Button) findViewById(R.id.go);
   	 
        currentLoc();
        
    	go.setOnClickListener(new android.view.View.OnClickListener() {
        	@Override
			public void onClick(android.view.View v) {
				// TODO Auto-generated method stub
        		if (Validate()){
        			map.clear();
        			if (Search() == 0){
        				Toast.makeText(getApplicationContext(), "No Venues were located", Toast.LENGTH_SHORT).show();
    					return;
        			}
        		}
        		else{
        			Toast.makeText(getApplicationContext(), "Please, add a Venue", Toast.LENGTH_SHORT).show();
        			
        			SharedPreferences settings =  getSharedPreferences("Search", MODE_PRIVATE);
        			SharedPreferences.Editor prefEditor = settings.edit();  
        			prefEditor.putString("Search", "false");
        			prefEditor.commit(); 
        			
					return;
        		}
			}
        });
    	
    	android.widget.Button clear = (android.widget.Button) findViewById(R.id.clear);
    	
    	clear.setOnClickListener(new android.view.View.OnClickListener() {
        	@Override
			public void onClick(android.view.View v) {
				// TODO Auto-generated method stub
        		currentLoc();
        		ClearText();
        		
        		SharedPreferences settings =  getSharedPreferences("Search", MODE_PRIVATE);
        		SharedPreferences.Editor prefEditor = settings.edit();  
        		prefEditor.putString("Search", "false");
        		prefEditor.commit(); 
        	}
        });
    	
    	SharedPreferences settingsError =  getSharedPreferences("Search", MODE_PRIVATE);
    	
    	if(settingsError.contains("true"))
		{
			Search();
		}
    	
//    	if (InvalidateApp()){
//        	Toast.makeText(getApplicationContext(), "This demo is expired", Toast.LENGTH_SHORT).show();
//        	finish();
//        }
    	
//    	android.widget.Button btnCap = (android.widget.Button) findViewById(R.id.btnTakeScreenshot);
//        btnCap.setOnClickListener(new android.view.View.OnClickListener() {
//
//            @Override
//            public void onClick(android.view.View v) {
//                // TODO Auto-generated method stub
//                try {
//                    CaptureMapScreen();
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    e.printStackTrace();
//                }
//
//            }
//        });
    	
        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }
	
//	private MapFragment getMapFragment() {
//	    FragmentManager fm = null; 
//	    //android.support.v4.app.FragmentManager fmm = null;
//	 
//	    try{
//	        fm = getFragmentManager();
//	    }catch(Exception e){
//	    	//fmm = getSupportFragmentManager(); 
//	    } 
//	    
////	    if (fmm == null){
////	    	fmm = getSupportFragmentManager(); 
////	    }
//	    
//	    MapFragment mm = null;
//	    
//	    if ((MapFragment) fm.findFragmentById(R.id.map1) == null){
//	    	mm = (MapFragment) ((MapFragment) fm.findFragmentById(R.id.map1)).getChildFragmentManager().findFragmentById(R.id.map1);
//	    }
//	 
//	    return ((MapFragment) (MapFragment) fm.findFragmentById(R.id.map1) == null) ? mm : (MapFragment) fm.findFragmentById(R.id.map1);
//	} 
	
	boolean InvalidateApp(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date futuredate = null;
		try {
			futuredate = sdf.parse("2014-10-24");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Date today = new Date(System.currentTimeMillis());
		
		return today.after(futuredate);
	}
	
	void ClearText(){
		android.widget.EditText et = (EditText) this.findViewById(R.id.editText1);
		et.setText("");
	}
	
	boolean Validate(){
		return (String)((android.widget.EditText)this.findViewById(R.id.editText1)).getText().toString().trim() != "";
	}
	
	void currentLoc(){
		map.clear();
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		try{
	        Criteria criteria = new Criteria();
	        String provider = service.getBestProvider(criteria, false);
	        location = service.getLastKnownLocation(provider); 
		}
		catch(Exception e){
			location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
		}
        //LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    	
    	map.addMarker(new MarkerOptions()
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
        .position(new LatLng(location.getLatitude(), location.getLongitude())));
	}
	
	int Search(){
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location location;
		
		try{
	        Criteria criteria = new Criteria();
	        String provider = service.getBestProvider(criteria, false);
	        location = service.getLastKnownLocation(provider); 
		}
		catch(Exception e){
			location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
		}
        
        //LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        VenuesCriteria vc = new VenuesCriteria();
        
        vc.setQuery(((android.widget.EditText)this.findViewById(R.id.editText1)).getText().toString() != "" ? 
        		(String) ((android.widget.EditText)this.findViewById(R.id.editText1)).getText().toString() : "");
        
        try {
        	arrayVenue = (ArrayList<Venue>) new AsyncTasks(this, new RequestListener(){
        		@Override
                public void onError(String errorMsg) {
                    Toast.makeText(map.this, "error", Toast.LENGTH_LONG).show();
                }
        	
        		@Override
        		public void onVenuesFetched(ArrayList<Venue> venues) {
        			//Toast.makeText(map.this, venues.toString(), Toast.LENGTH_LONG).show();
        		}
        	}, vc, location).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try{
        	String name = ((Venue)arrayVenue.get(0)).getName();
        }
        catch(Exception e){
        	ClearText();
        	return 0;
        }
                
        currentLoc();
        
        for(int i = 0; i < arrayVenue.size(); i++) {
        	
        	Venue v = arrayVenue.get(i);
        	        	
        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 14));
        	
        	final Marker m = map.addMarker(new MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag))
        	.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
            .title(v.getName())
        	.snippet(v.getSnipped())
            .position(new LatLng(v.getLatitude(), v.getLongitude())));
        	        	
        	final ValueAnimator va = ValueAnimator.ofFloat(20, 1);
        	va.setDuration(new Random().nextInt(2500));
        	va.setInterpolator(new AccelerateInterpolator());
        	va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        	    @Override
        	    public void onAnimationUpdate(ValueAnimator animation) {
        	        m.setAnchor(0f, (Float) animation.getAnimatedValue());
        	    }
        	});
        	va.start();
        }
        
        SharedPreferences settings =  getSharedPreferences("Search", MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();  
		prefEditor.putString("Search", "true");  
		prefEditor.commit(); 
        
        return arrayVenue.size();
	}
	
	public void HandleException()
	{
		// Remove exception
		SharedPreferences settings = getSharedPreferences("UncaughtException", MODE_PRIVATE);
 	  	SharedPreferences.Editor prefEditor = settings.edit();  
   		prefEditor.remove("Exception");
   		prefEditor.commit(); 
   		
   		if(!isOnline())
   		{
   			NoInternetAlert();
   		}
   		else 
   		{
			new AlertDialog.Builder(this) 
	        .setMessage(String.format(getResources().getString(R.string.exceptionmessage),getResources().getString(R.string.supportEmail))) 
	        .setTitle(R.string.exceptiontitle) 
	        .setCancelable(false) 
	        .setNegativeButton(R.string.exceptionbuttontext, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	        })
	        .show();
   		}
	}
	
	public Boolean isOnline() 
	{
		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
	}

	public void NoInternetAlert()
	{
			new AlertDialog.Builder(this) 
	        .setMessage(getResources().getString(R.string.connectotinternet)) 
	        .setTitle(R.string.couldnotconnecttoserver) 
	        .setCancelable(false) 
	        .setNegativeButton(R.string.exceptionbuttontext, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	        })
	        .show();
	}
	
//	public void CaptureMapScreen() 
//	{
//	SnapshotReadyCallback callback = new SnapshotReadyCallback() {
//	            Bitmap bitmap;
//
//	            @Override
//	            public void onSnapshotReady(Bitmap snapshot) {
//	                // TODO Auto-generated method stub
//	                bitmap = snapshot;
//	                try {
////	                    FileOutputStream out = new FileOutputStream("/mnt/sdcard/"
////	                        + "MyMapScreen" + System.currentTimeMillis()
////	                        + ".png");
////
////	                    // above "/mnt ..... png" => is a storage path (where image will be stored) + name of image you can customize as per your Requirement
////
////	                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//	                	String path = Environment.getExternalStorageDirectory().toString();
//	        			OutputStream fOut = null;
//	        	        File file = new File(path, "mapscs.jpeg");
//	        	        //String location = file.getPath();
//	        	        fOut = new FileOutputStream(file);
//	        	        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
//	        	        fOut.flush();
//	        	        fOut.close();
//	        	        
//	        	        MediaScannerConnection.scanFile(getApplicationContext(), 
//	            				new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, 
//	            				null,
//	            				new MediaScannerConnection.OnScanCompletedListener() {
//	                        @Override
//	                        public void onScanCompleted(String path, Uri uri) {
//	                            // TODO Auto-generated method stub
//	                        }
//	                    });
//	        	        
//	                } catch (Exception e) {
//	                    e.printStackTrace();
//	                }
//	            }
//	        };
//
//	        map.snapshot(callback);
//	        
//	        // myMap is object of GoogleMap +> GoogleMap myMap;
//	        // which is initialized in onCreate() => 
//	        // myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
//	}
}