package uk.asimrehman.crimemap.Models;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by admin on 01/10/2016.
 */

public class Location extends SugarRecord{

    //Initialize private variables
    Double _latitude;
    Double _longitude;

    int _streetid;
    String _streetName;

    //Empty constructor
    public Location()
    {

    }

    public Location(Double _latitude, Double _longitude, int _streetid, String _streetName)
    {
        this._latitude = _latitude;
        this._longitude = _longitude;

        this._streetid = _streetid;
        this._streetName = _streetName;
    }

    //Provide get and set methods

    //Lng / Lat Methods
    public Double get_latitude() {
        return _latitude;
    }

    public void set_latitude(Double _latitude) {
        this._latitude = _latitude;
    }

    public Double get_longitude() {
        return _longitude;
    }

    public void set_longitude(Double _longitude) {
        this._longitude = _longitude;
    }


    //Street info methods
    public int get_streetid() {
        return _streetid;
    }

    public void set_streetid(int _streetid) {
        this._streetid = _streetid;
    }

    public String get_streetName() {
        return _streetName;
    }

    public void set_streetName(String _streetName) {
        this._streetName = _streetName;
    }


    public List<Crimes> getCrimes()
    {
        return Crimes.find(Crimes.class, "location = ?", new String[]{getId().toString()});
    }
}
