package uk.asimrehman.crimemap.businesslogiclayer;

import java.util.List;

import uk.asimrehman.crimemap.Models.Location;

/**
 * Created by admin on 19/12/2016.
 * Adds or gets new location into the database
 */

public class LocationBLL extends GenericBLL<Location> {


    public LocationBLL()
    {
        super(Location.class);
    }


    public static Location GetLocationByStreetId(int streetid)
    {
        List<Location> getLocations = Location.find(Location.class, " _streetid = ?", String.valueOf(streetid));

        if(getLocations.size()>0)
        {
            return getLocations.get(0);
        }

        return null;
    }

}
