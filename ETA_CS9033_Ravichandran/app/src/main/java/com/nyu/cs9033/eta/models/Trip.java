package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {
	
	// Member fields should exist here, what else do you need for a trip?
	// Please add additional fields
	private String name;
    String friends;
    String destination;
    String date;
    String time;
    String tripid;
    String dest_addr;
    String dest_lat;
    String dest_longi;


    //Constructor
    public Trip() {
        this.name = null;
        this.friends = null;
        this.destination = null;
        this.date = null;
        this.time = null;
        this.tripid = null;
        this.dest_addr = null;
        this.dest_lat = null;
        this.dest_longi = null;

    }
    public void setTripid (String tripid) { this.tripid = tripid;}
    public String getTripid(){
        return this.tripid;
    }
    public void setName(String tripname){
        this.name = tripname;
    }

    public String getName(){
        return this.name;
    }

    public void setFriends(String friends){
        this.friends = friends;
    }

    public String getFriends(){
        return this.friends;
    }

    public void setDestination(String location){
        this.destination = location;
    }

    public String getDestination(){
        return this.destination;
    }

    public void setDestAddr(String addr){
        this.dest_addr = addr;
    }

    public String getDestAddr(){
        return this.dest_addr;
    }
    public void setDestLat(String latitude){
        this.dest_lat = latitude;
    }

    public String getDesLat(){
        return this.dest_lat;
    }

    public void setDestLongi(String longitude){
        this.dest_longi = longitude;
    }

    public String getDestLongi(){
        return this.dest_longi;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return this.date;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return this.time;
    }











	/**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};
	
	/**
	 * Create a Trip model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Trip(Parcel p) {
        this.name = p.readString();
        this.friends = p.readString();
        this.destination = p.readString();
        this.date = p.readString();
        this.time = p.readString();
        this.tripid = p.readString();
	}
	
	/**
	 * Create a Trip model object from arguments
	 * 
	 * @param name  Add arbitrary number of arguments to
	 * instantiate Trip class based on member variables.
	 */
	public Trip(String name) {
		
		// TODO - fill in here, please note you must have more arguments here
	}

	/**
	 * Serialize Trip object by using writeToParcel. 
	 * This function is automatically called by the
	 * system when the object is serialized.
	 * 
	 * @param dest Parcel object that gets written on 
	 * serialization. Use functions to write out the
	 * object stored via your member variables. 
	 * 
	 * @param flags Additional flags about how the object 
	 * should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 * In our case, you should be just passing 0.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.friends);
        dest.writeString(this.destination);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.tripid);
	}
	
	/**
	 * Feel free to add additional functions as necessary below.
	 */
	
	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}
