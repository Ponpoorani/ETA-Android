package com.nyu.cs9033.eta.controllers;

/**
 * The MainActivity.java is the initial UI of application.
 * There is a mechanism to navigate to the other
 * activities, CreateTrip and ViewTrip.
 *
 * @author  Ponpoorani Ravichandran
 * @version 2.0
 *
 */

import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    Trip trip;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Event Listener for Create Trip Button
        Button createTripBtn = (Button) findViewById(R.id.createTrip);
        createTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreateTripActivity();
            }
        });

        // Event Listener for View Trip Button
        Button viewTripBtn = (Button) findViewById(R.id.viewTrip);
        viewTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startViewTripActivity();
            }
        });
    }

    /**
     * This method should start the
     * Activity responsible for creating
     * a Trip.
     */
    public void startCreateTripActivity() {
        Intent intent = new Intent(MainActivity.this, CreateTripActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * This method should start the
     * Activity responsible for viewing
     * a Trip.
     */
    public void startViewTripActivity() {
        Intent intent = new Intent(MainActivity.this, TripList.class);
        startActivity(intent);
    }

    public void currentTrip(View view){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String tripID = sharedPrefs.getString("prefTripID", "");

        Log.d("trip-Main activity", tripID);
        if(tripID==null || tripID.isEmpty())
        {
            Intent ErrorActivityIntent=new Intent(this,ErrorActivity.class);
            startActivity(ErrorActivityIntent);
        }
        else
        {
            Intent viewintent = new Intent(this, CurentTripActivity.class);
            viewintent.putExtra("tripID",tripID);
            startActivity(viewintent);
        }
    }

}
