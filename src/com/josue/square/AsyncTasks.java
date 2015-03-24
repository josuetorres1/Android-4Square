/**
 * 
 */
package com.josue.square;

/**
 * @author jtorres
 *
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import android.location.Location;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.net.ssl.SSLPeerUnverifiedException;

public class AsyncTasks extends
AsyncTask<String, Integer, ArrayList<Venue>>{
	
	public static final String CLIENT_ID = "##############################################"; 
    public static final String CLIENT_SECRET = "#############################################"; 

	public static final String CALLBACK_URL = "http://localhost:8888";
    public static final String SHARED_PREF_FILE = "shared_pref";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_INFO = "user_info";
    public static final String API_DATE_VERSION = "20140714";
    
    private Activity mActivity;
    private ProgressDialog mProgress;
    private RequestListener mListener;
    private VenuesCriteria mCriteria;
    private boolean sslExp;
    private Location mlocation;

    public AsyncTasks(Activity activity,
            RequestListener listener, VenuesCriteria criteria, Location location) {
    	
    		mActivity = activity;
    		mListener = listener;
    		mCriteria = criteria;
    		mlocation = location;
    }
    
    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mActivity);
        mProgress.setCancelable(false);
        mProgress.setMessage("Getting venues nearby ...");
        mProgress.show();
        super.onPreExecute();
    }
    
	@Override
	protected ArrayList<Venue> doInBackground(String... params) {
		// TODO Auto-generated method stub
		ArrayList<Venue> venues = new ArrayList<Venue>();
		Venue item;
				
        try {

            //date required
            String apiDateVersion = API_DATE_VERSION;
            // Call Foursquare to get the Venues around
            String uri = "https://api.foursquare.com/v2/venues/search"
                    	+ "?client_id=" + CLIENT_ID + "&client_secret="
                    	+ CLIENT_SECRET + "&v=" 
                    	+ apiDateVersion + "&ll=" 
                    	//+ mCriteria.getLocation().getLatitude()
                    	+ mlocation.getLatitude()
                        + "," 
                    	//+ mCriteria.getLocation().getLongitude() 
                        + mlocation.getLongitude()
                        + "&query="
                        + mCriteria.getQuery().trim();// + mCriteria.getQuery();
            
            JSONObject venuesJson = executeHttpGet(uri);

            // Get return code
            int returnCode = Integer.parseInt(venuesJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == 200) {
                Gson gson = new Gson();
                JSONArray json = venuesJson.getJSONObject("response")
                        .getJSONArray("venues");
//                for (int i = 0; i < json.length(); i++) {
//                    Venue venue = gson.fromJson(json.getJSONObject(i)
//                            .toString(), Venue.class);
//                    venues.add(venue);
//                }
            	for (int i = 0; i < json.length(); i++) {
            		item = new Venue(); 
            		Double la = Double.parseDouble(json.getJSONObject(i).getJSONObject("location").getString("lat").trim());
            		Double lo = Double.parseDouble(json.getJSONObject(i).getJSONObject("location").getString("lng").trim());
            		item.setLatitude(la);
            		item.setLongitud(lo);
            		try{
            			item.setName(json.getJSONObject(i).getString("name").trim());
            		}
            		catch(JSONException j)
            		{
            			item.setName("");
            		}
            		try{
            			item.setSnipped(json.getJSONObject(i).getJSONObject("location").getString("address").trim());
            		}
            		catch(JSONException j){
            			item.setSnipped("");
            		}
            		venues.add(item);
            	}
            	} else {
            		if (mListener != null)
            			mListener.onError(venuesJson.getJSONObject("meta")
                            .getString("errorDetail"));
            	}

        } catch (SSLPeerUnverifiedException sslExp) {
            this.sslExp = true;
            sslExp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
            if (mListener != null)
                mListener.onError(exp.toString());
        }
        return venues;
	}
	
	@Override
    protected void onPostExecute(ArrayList<Venue> venues) {
		super.onPostExecute(venues);
        if (sslExp) {
            Toast.makeText(mActivity, "You must log in to the Wifi network first, " +
                    "or disconnect from it and use cellular connection.", Toast.LENGTH_LONG).show();
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com")));
        }

        mProgress.dismiss();
        if (mListener != null)
            mListener.onVenuesFetched(venues);
    }
	
	// Calls a URI and returns the answer as a JSON object
    private JSONObject executeHttpGet(String uri) throws Exception {
        HttpGet req = new HttpGet(uri.replace(" ", "_"));

        HttpClient client = new DefaultHttpClient();
        HttpResponse resLogin = client.execute(req);
        BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }

        return new JSONObject(sb.toString());
    }

}
