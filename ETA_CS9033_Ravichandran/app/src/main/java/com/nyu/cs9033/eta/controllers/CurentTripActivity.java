package com.nyu.cs9033.eta.controllers;
/**
 * The ViewTripActivity.java is used
 * to display trip information.
 *
 * @author  Ponpoorani Ravichandran
 * @version 2.0
 *
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class CurentTripActivity extends Activity {
    //Variable declaration
    Trip trip;
    GPSTracker gps;
    private static JSONArray distanceLeft;
    private static JSONArray timeLeft;
    private static JSONArray peopleInvolved;
	private static final String TAG = "CurrentTripActivity";
    static String tripID;
    private static TextView text_distleft,time_left,text_people;
	TextView tripname,friends,destination,date,time;

    TextView tripnamelabel,text_date,text_time,text_friends,location,text_error;
    ImageView image;
    private static final int POLL_INTERVAL = 1000 * 5;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.current_trip_activity);

		tripname = (TextView)findViewById(R.id.edit_tripname);
        date = (TextView)findViewById(R.id.edit_date);
        time = (TextView)findViewById(R.id.edit_time);
        destination = (TextView)findViewById(R.id.edit_location);
        friends = (TextView)findViewById(R.id.edit_friends);
        tripnamelabel= (TextView)findViewById(R.id.tripnamelabel);
        text_date= (TextView)findViewById(R.id.date);
        text_time= (TextView)findViewById(R.id.time);
        text_friends=(TextView)findViewById(R.id.friends);
        location=(TextView)findViewById(R.id.location);
        image=(ImageView)findViewById(R.id.oops);
        text_error=(TextView) findViewById(R.id.text_error);

        text_people = (TextView)findViewById(R.id.text_people);
        text_distleft = (TextView)findViewById(R.id.text_distleft);
        time_left = (TextView)findViewById(R.id.time_left);

        tripID = getTripID(getIntent());

        trip = viewTrip(tripID);

        // Event Listener for back Button
        Button backButton = (Button)findViewById(R.id.backbtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CurentTripActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Event Listener for Trip status Button
        Button tripstatusbtn = (Button)findViewById(R.id.tripstatusbtn);
        tripstatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpAsyncTask().execute("http://cs9033-homework.appspot.com/");
            }
        });

        // Event Listener for current location Button
        Button current_loc = (Button)findViewById(R.id.current_loc);
        current_loc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                new HttpAsyncTaskforLocationUpdate().execute("http://cs9033-homework.appspot.com/");
            }
        });

        Button tripStartButton = (Button)findViewById(R.id.starttripbtn);
        tripStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getBaseContext(), "Trip Finished! Bye!", Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("prefTripID", "");
                Boolean flag = editor.commit();

                Intent intent = new Intent(CurentTripActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //new HttpAsyncTaskforLocationUpdate().execute("http://cs9033-homework.appspot.com/");
            }
        });


	}

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog pDialog;
        Trip trip;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(CurentTripActivity.this);
            pDialog.setMessage("Fetching Trip...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            String tripIdentifier =  POST(urls[0]) ;

            Log.d("trip_id",tripIdentifier);
            return tripIdentifier;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String tID) {
            pDialog.dismiss();


        }

    }

    public String POST(String url) {

        InputStream inputStream = null;
        String result = "";
        String jsonString = "";
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "TRIP_STATUS");
            jsonObject.put("trip_id", tripID);


            Log.d("Final json:", jsonObject.toString());

            jsonString = jsonObject.toString();
            StringEntity se = new StringEntity(jsonString);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
            JSONObject jObject = new JSONObject(result);
            Log.d("msg","Response code : " + jObject);

            distanceLeft = jObject.getJSONArray("distance_left");
            timeLeft = jObject.getJSONArray("time_left");
            peopleInvolved = jObject.getJSONArray("people");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    text_people.setText("People: " + peopleInvolved.toString());
                    text_distleft.setText("Distance Left: " + distanceLeft.toString());
                    time_left.setText("Time Left: " + timeLeft.toString());
                }
            });


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return tripID;
    }

    private class HttpAsyncTaskforLocationUpdate extends AsyncTask<String, Void, String> {
        double latitude;
        double longitude;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gps = new GPSTracker(CurentTripActivity.this);

            if(gps.canGetLocation()){
                latitude  = gps.getLatitude();
                longitude = gps.getLongitude();
            }
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream inputStream = null;
            String result = "";

            String jsonString = "";
            Calendar c = Calendar.getInstance();

            try{
                DefaultHttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(urls[0]);
               String date=trip.getDate();
                String time=trip.getTime();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("command", "UPDATE_LOCATION");
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("datetime", date+" "+time);
                jsonString = jsonObject.toString();
                Log.d("json obj",jsonString);
                StringEntity se = new StringEntity(jsonString);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();


                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();

                JSONObject jObject = new JSONObject(result);
                String responseCode = jObject.getString("response_code");
                Log.d("Response Code:",responseCode);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            String addr = getAddress(latitude, longitude);
           /* Log.d("address",addr);*/

            return addr;
        }

        @Override
        protected void onPostExecute(String address) {
            Toast.makeText(getBaseContext(), "Current Location: "+address, Toast.LENGTH_LONG).show();
        }

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                //result.append(address.getLocality()).append("\n");
                //result.append(address.getCountryName());
                result.append(address.getAddressLine(0)).append(",");
                result.append(address.getLocality()).append(",");
                result.append(address.getAdminArea()).append(",");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }


    /**
	 * Create a Trip object via the recent trip that
	 * was passed to TripViewer via an Intent.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */
	public String getTripID(Intent i) {
        // get value from intent
        String tripid = i.getStringExtra("tripID");
        Log.d("tripID", tripid);

        if (tripid!=null) {
            return tripid;
        }
		return null;
	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip_id The Trip model used to
	 * populate the View.
	 */
	public Trip viewTrip(String trip_id) {
        TripDatabaseHelper helper = new TripDatabaseHelper(getBaseContext());
        Trip trip = helper.getTripDetails(trip_id);
        tripname.setText(trip.getName());
        friends.setText(trip.getFriends());
        destination.setText(trip.getDestination());
        date.setText(trip.getDate());
        time.setText(trip.getTime());

        return trip;
	}

    public class AlarmReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            new HttpAsyncTaskforLocationUpdate().execute("http://cs9033-homework.appspot.com/");
            Toast.makeText(context, "Location Updated", Toast.LENGTH_LONG).show();
        }

    }
}
