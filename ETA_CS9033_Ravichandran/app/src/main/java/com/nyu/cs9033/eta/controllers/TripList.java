package com.nyu.cs9033.eta.controllers;

/**
 * The TripList.java is used
 * to display details about all trips.
 *
 * @author  Ponpoorani Ravichandran
 * @version 1.0
 *
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.util.Log;
import com.nyu.cs9033.eta.models.Trip;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.nyu.cs9033.eta.R;
import android.view.LayoutInflater;
import android.widget.Toast;


public class TripList extends ListActivity implements OnItemClickListener{
    ArrayList<String> ItemList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        populateList();
     /*   if(tripList.isEmpty())
            Toast.makeText(getBaseContext(), "No trips to show!", Toast.LENGTH_SHORT).show();*/

        ListView lv = this.getListView();

        ArrayAdapter<String> adptr = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ItemList);
        lv.setAdapter(adptr);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(this);

        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.header, lv, false);
        lv.addHeaderView(header, null, false);

    }
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        String splitter = ((TextView)arg1).getText().toString();
        System.out.println("splitter" + splitter);

        String[] split = new String[2];
        split = splitter.split(" ");

        String Trip_id = split[1].toString();
        Log.d("split" , Trip_id);


        TripDatabaseHelper helper = new TripDatabaseHelper(getBaseContext());
        Trip trip = helper.getTripDetails(Trip_id);


        Intent viewintent = new Intent(this, ViewTripActivity.class);
        viewintent.putExtra("tripID",Trip_id);
        startActivity(viewintent);


    }

    //Populate trip information from the database with
    //the help of TripDatabaseHelper
    public void populateList(){
        TripDatabaseHelper helper = new TripDatabaseHelper(getBaseContext());
        List<Trip> tripList = new ArrayList<Trip>();
        tripList = helper.getAllTrips();
        if(tripList.isEmpty())
        {

            Intent ErrorActivityIntent=new Intent(this,ErrorActivity.class);
            startActivity(ErrorActivityIntent);
        }

        String tmpstr = new String();
        Trip tmpobj = new Trip();
        for(Iterator<Trip> itr = tripList.iterator();itr.hasNext();){
            tmpobj = (Trip)itr.next();
            tmpstr = "ID: " + tmpobj.getTripid().toString() ;
            ItemList.add(tmpstr);
        }

    }

}