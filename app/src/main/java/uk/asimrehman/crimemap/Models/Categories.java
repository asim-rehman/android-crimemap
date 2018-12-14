package uk.asimrehman.crimemap.Models;

import com.orm.SugarRecord;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by admin on 19/12/2016.
 */

public class Categories extends SugarRecord {

    String url;
    String name;
    int colour;

    public Categories() {
    }

    public Categories(String url, String name, int colour) {
        this.url = url;
        this.name = name;
        this.colour = colour;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }


    public List<Crimes> GetCrimes()
    {
        return Crimes.find(Crimes.class, "category = ?", new String[]{getId().toString()});
    }

    public List<Crimes> GetCrimes(long SearchHistoryID, DateTime _date)
    {
        return Crimes.find(Crimes.class, "category = ? and Search_History =? and _Date=?", new String[]{getId().toString(), String.valueOf(SearchHistoryID), _date.toString("Y-MM")});
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
