package uk.asimrehman.crimemap.Models;

import com.orm.SugarRecord;

/**
 * Created by admin on 01/10/2016.
 */

//This entity class is used to add crimes to the database, we use SugarRecord ORM
//Which will provide us with the data access methods
public class Crimes extends SugarRecord {

    //Initialize private variables
    int _crimeid;


    String _persistendid;
    String _outcome;
    String _date;


    //Empty constructor
    public Crimes()
    {

    }

    public Crimes(int _crimeid, String _persistendid, String _outcome)
    {
        this._crimeid = _crimeid;
        this._persistendid = _persistendid;
        this._outcome = _outcome;
    }

    //Provide get and set methods
    public int get_crimeid() {
        return _crimeid;
    }

    public void set_crimeid(int _crimeid) {
        this._crimeid = _crimeid;
    }

    public String get_persistendid() {
        return _persistendid;
    }

    public void set_persistendid(String _persistendid) {
        this._persistendid = _persistendid;
    }


    public String get_Date() {
        return _date;
    }

    public void set_Date(String _date) {
        this._date = _date;
    }

    public String get_outcome() {
        return _outcome;
    }

    public void set_outcome(String _outcome) {
        this._outcome = _outcome;
    }


    public Categories Category;
    public Location Location;
    public SearchHistory SearchHistory;
}
