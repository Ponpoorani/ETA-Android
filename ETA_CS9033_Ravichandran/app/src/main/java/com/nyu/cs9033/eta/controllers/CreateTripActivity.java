package com.nyu.cs9033.eta.controllers;

/**
 * The CreateTripActivity.java allow users to create trip with a location, date,
 * friends, time and trip name.
 *
 * @author  Ponpoorani Ravichandran
 * @version 2.0
 *
 */
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateTripActivity extends Activity {

    // variable declaration

    private static Integer responseCode;
    private static final String TAG = "CreateTripActivity";
    static final int PICK_CONTACT=1, SET_DATE=2, SET_TIME=3;
    static final int PICK_LOCATION=4;
    EditText edit_friends,edit_location,edit_date,edit_time,edit_tripname;
    EditText txtDate, txtTime;
    static ArrayList<String> arrayList=new ArrayList<String>();
    String contactarray;
    String dest_addr="", dest_lat="", dest_longi="";

    // Variable for storing current date and time
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_trip_activity);

        // Event Listener for Create Trip Button
        Button createTripBtn = (Button) findViewById(R.id.createTripButton);
        createTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter all fields!", Toast.LENGTH_LONG).show();
                else
                    new HttpAsyncTask().execute("http://cs9033-homework.appspot.com/");

            }// on click close
        });

        // Event Listener for Cancel Trip Button
        Button cancelTripBtn = (Button) findViewById(R.id.cancelTripButton);
        cancelTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTrip();
            }
        });

        txtTime = (EditText) findViewById(R.id.edit_time);
        txtDate = (EditText) findViewById(R.id.edit_date);

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog pDialog;
        Trip trip;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(CreateTripActivity.this);
            pDialog.setMessage("Creating Trip...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            trip = createTrip();


            String tripIdentifier =  POST(urls[0],trip) ;

            Log.d("trip_id",tripIdentifier);
            return tripIdentifier;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String tID) {
            pDialog.dismiss();

           TripDatabaseHelper helper = new TripDatabaseHelper(getBaseContext());
            helper.insertTripInfo(trip);
            Boolean tripStatus = saveTrip(trip);
            if (!tripStatus)
                finish();

            Intent goToMainIntent = new Intent(CreateTripActivity.this, MainActivity.class);
            startActivity(goToMainIntent);
        }

    }

    public static String POST(String url, Trip  trip) {

        InputStream inputStream = null;
        String result = "";
        String jsonString = "";
        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            JSONArray tripLocation = new JSONArray();
            tripLocation.put(trip.getDestination());
            tripLocation.put(trip.getDestAddr());
            tripLocation.put(trip.getDesLat());
            tripLocation.put(trip.getDestLongi());
            String date=trip.getDate();
            String time=trip.getTime();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "CREATE_TRIP");
            jsonObject.put("location", tripLocation);
            jsonObject.put("datetime", date + " " + time);
            jsonObject.put("people", arrayList.toString());

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
            responseCode = jObject.getInt("trip_id");

            if (responseCode < 0) {
                responseCode = responseCode * -1;
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        trip.setTripid(responseCode.toString());
        // 11. return result

        Log.d("msg","Response code : " + responseCode);

        return trip.getTripid();
    }

    private boolean validate(){
 /*       edit_tripname=(EditText)findViewById(R.id.edit_tripname);
        edit_friends=(EditText)findViewById(R.id.edit_friends);
        edit_location=(EditText)findViewById(R.id.edit_location);
        edit_date=(EditText)findViewById(R.id.edit_date);
        edit_time=(EditText)findViewById(R.id.edit_time);

        if(edit_friends.getText().toString().trim().equals(""))
            return false;
        else if(edit_location.getText().toString().trim().equals(""))
            return false;
        else if(edit_time.getText().toString().trim().equals(""))
            return false;
        else if(edit_date.getText().toString().trim().equals(""))
            return false;
        else if(edit_tripname.getText().toString().trim().equals(""))
            return false;
        else
            return true;*/
    return true;
    }


    /*
     * Method for extracting contact number of friends from contacts.
     */
    public String extract_number(Cursor c,String id){
        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

        if (hasPhone.equalsIgnoreCase("1")) {
            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                    null, null);
            String cNumber = null;
            while (phones.moveToNext()) {
                cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            return cNumber;
        }
        return null;
    }

    /*
     * Method for extracting email address of friends from contacts.
     * @(param) cursor
     * @{param} String
     */
    public String extract_email(Cursor c,String id){
        Cursor emails = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                        + " = " + id, null, null);
        if(emails.moveToFirst())
        {
            String emailAddress = emails.getString(emails.getColumnIndex("data1"));
            return emailAddress;
        }
        return null;
    }

    // function to invoke contact list(picker)
    // @{param} view object
    public void addFriends(View view){
        Intent intentContact=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentContact,PICK_CONTACT);
    }

    // function to choose the date from datepicker
    // @{param} view object
    public void addDate(View view){
        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in edittext
                        txtDate.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }
    // function to choose the time from timepicker
    // @{param} view object
    public void addTime(View view){

        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox
                        txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        tpd.show();
    }

    /*
     * Method to call HW3API(implicit intent) with searchVal param,
     * to get location information.
     * @{param} view object
     */
    public void addLocation(View view){

        EditText Location = (EditText) findViewById(R.id.edit_location);
        String uriName = "location://com.example.nyu.hw3api";
        Intent locationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriName));
        locationIntent.putExtra("searchVal",Location.getText().toString());

        startActivityForResult(locationIntent, PICK_LOCATION);
    }

    /*
    * Method to select multiple contacts from contact list
    * and store it in the ArrayList
    * @{param} int requestcode
    * @{param} int resultcode
    * @{param} Intent data
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri contactData = data.getData();
            Cursor c =  getContentResolver().query(contactData, null, null, null, null);

            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (!arrayList.contains(name)) {
                    arrayList.add(name);
                }

                Log.d("arraylist","Start of for loop");
                for (int i=0; i<arrayList.size();i++) {

                    Log.d("arraylist -- ",  i + arrayList.get(i).toString());
                }

                StringBuilder sb = new StringBuilder();

                for(String c_name : arrayList){
                    sb.append(c_name + ",");
                }


                edit_friends = (EditText) findViewById(R.id.edit_friends);
                edit_friends.setText(sb.toString().substring(0,sb.toString().length()-1));



/*                String cname=edit_friends.getText().toString();


                if(cname.matches("")) {
                    edit_friends.setText(name);
                }
                else {
                    edit_friends.setText(cname+","+name);
                }*/



                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                //String cNumber = extract_number(c, id);
//                String eAddress = extract_email(c, id);
//
//                // create person object to transfer data...
//
                Person p = new Person();
                p.setPersonName(name);

                //p.setPersonNumber(cNumber);
//                p.setPersonEmail(eAddress);
            }
        }
        if (requestCode == PICK_LOCATION) {
            ArrayList locInfo= new ArrayList();
            EditText Location = (EditText) findViewById(R.id.edit_location);

            locInfo = data.getExtras().getStringArrayList("retVal");
            Location.setText(locInfo.get(0).toString()); // set location name

            dest_addr = locInfo.get(1).toString(); // get location address
            dest_lat = locInfo.get(2).toString(); // get location latitude
            dest_longi = locInfo.get(3).toString(); // get location longitude
        }
    }

    /**
     * This method should be used to
     * instantiate a Trip model object.
     * Used to take the values from the textbox
     * and store into the trip object
     * @return The Trip as represented
     * by the View.
     */

    public Trip createTrip() {
        Trip trip = new Trip();

        edit_tripname=(EditText)findViewById(R.id.edit_tripname);
        edit_friends=(EditText)findViewById(R.id.edit_friends);
        edit_location=(EditText)findViewById(R.id.edit_location);
        edit_date=(EditText)findViewById(R.id.edit_date);
        edit_time=(EditText)findViewById(R.id.edit_time);

        trip.setName(edit_tripname.getText().toString());
        trip.setFriends(edit_friends.getText().toString());
        trip.setDestination(edit_location.getText().toString());
        trip.setDestAddr(dest_addr);
        trip.setDestLat(dest_lat);
        trip.setDestLongi(dest_longi);
        trip.setDate(edit_date.getText().toString());
        trip.setTime(edit_time.getText().toString());
        //trip.setDatetime(edit_date.getText().toString()+""+edit_time.getText().toString());
        return trip;
    }

    /**
     * For HW2 you should treat this method as a
     * way of sending the Trip data back to the
     * main Activity.
     *
     * Note: If you call finish() here the Activity
     * will end and pass an Intent back to the
     * previous Activity using setResult().
     *
     * @return whether the Trip was successfully
     * saved.
     */

    //Going back to Main activity and destroying the create trip activity
    public boolean saveTrip(Trip trip) {
        if(trip!=null)
        {
            //Toast.makeText(getApplicationContext(), "Trip successfully created", Toast.LENGTH_SHORT).show();

            Intent mainActivityIntent=new Intent(this,MainActivity.class);

            mainActivityIntent.putExtra("Trip",trip);
            setResult(Activity.RESULT_OK,mainActivityIntent);
            finish();
            return true;
        }
        return false;
    }

    /**
     * This method should be used when a
     * user wants to cancel the creation of
     * a Trip.
     *
     * Note: You most likely want to call this
     * if your activity dies during the process
     * of a trip creation or if a cancel/back
     * button event occurs. Should return to
     * the previous activity without a result
     * using finish() and setResult().
     */
    public void cancelTrip() {

        Intent mainActivityIntent=new Intent(this,MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
