package uk.asimrehman.crimemap.businesslogiclayer;


import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;


import java.util.ArrayList;
import java.util.List;

import uk.asimrehman.crimemap.Core.PoliceAPI;
import uk.asimrehman.crimemap.CrimeMapFragment;
import uk.asimrehman.crimemap.Models.Crimes;
import uk.asimrehman.crimemap.Models.Location;
import uk.asimrehman.crimemap.Models.SearchHistory;


/**
 * Created by admin on 05/10/2016.
 */

/**
 * Class used to gets all crimes
 */
public class CrimesBLL extends GenericBLL<Crimes> {


    Double lat;
    Double lng;
    String date;
    long searchHistoryid;
    SearchHistory searchHistory;

    CrimeMapFragment.GetCrimeData crimeData;
    int _responseCode;

    public CrimesBLL()
    {
        super(Crimes.class);
    }

    /**
     * Class used for adding and retrieving crimes
     * @param lat Latitude of location
     * @param lng Longitude of location
     * @param date Date to get crimes for (only month and year is used, but pass full date)
     * @param searchHistoryid ID of search history (if exists it is taken from DB instead of making external API requests)
     * @param task The async task in CrimeMapFragment
     */
    public CrimesBLL(Double lat, Double lng, String  date, long searchHistoryid, CrimeMapFragment.GetCrimeData task) {
        super(Crimes.class);
        this.lat = lat;
        this.lng = lng;
        this.date = date;
        this.searchHistoryid = searchHistoryid;
        this.crimeData = task;

        searchHistory = SearchHistory.findById(SearchHistory.class, searchHistoryid);
    }

    /**
     * Gets crime information data from Police API UK
     * @return List of crimes
     */
    public List<Crimes> GetCrimeData() {

        List<Crimes> CrimesList = new ArrayList<>();

            try {

                PoliceAPI policeAPI = new PoliceAPI(crimeData);
                CrimesList = policeAPI.GetCrimes(lat, lng, date);
                _responseCode = policeAPI.get_responseCode();

            } catch (Exception e) {
                Log.d("Error",e.getMessage());
            }


        return CrimesList;


    }

    /**
     * Saves a list of crime information into the database
     * @param CrimesList The list of crimes to save
     */
    public void SaveCrimeData(List<Crimes> CrimesList)
    {
        for (int i = 0; i < CrimesList.size(); i++) {
            crimeData.PublishProgress("Saving information " + i + " of " + CrimesList.size());
            Crimes crime = CrimesList.get(i);

            Location doesLocationExist = LocationBLL.GetLocationByStreetId(crime.Location.get_streetid());

            if (doesLocationExist != null)
                crime.Location = doesLocationExist;


            crime.Location.save();
            crime.Category.save();
            crime.SearchHistory = searchHistory;
            crime.save();
        }
    }

    /**
     * Check if crimes exist for a given search history and date
     * @param _searchHistoryID The ID of the sarch
     * @param date The date of crime to search
     * @return true if crime exist or false
     */
    public static Boolean DoCrimesExistForSearchHistory(long _searchHistoryID, DateTime date)
    {
        return Crimes.count(Crimes.class, " Search_History =? and _Date = ?", new String[]{String.valueOf(_searchHistoryID), date.toString("Y-MM")}) != 0;
    }

    /**
     *
     * @param _searchHistoryID
     * @param date
     * @param ignoredCategories
     * @return
     */
    public static List<Crimes> GetCrimesForCategories(long _searchHistoryID, DateTime date, ArrayList<Long> ignoredCategories)
    {

        List<Crimes> allCrimes = Crimes.find(Crimes.class, " Search_History =? and _Date = ? AND CATEGORY IN ("+TextUtils.join(",", ignoredCategories)+")",
                String.valueOf(_searchHistoryID), date.toString("Y-MM"));
        return allCrimes;
    }

    /**
     * Get list of crimes by search history id and date
     * @param _searchHistoryID ID of search history
     * @param _date Date of crime to search for
     * @return List of crimes
     */
    public static List<Crimes> GetCrimesBySearchHistoryAndDate(long _searchHistoryID, DateTime _date)
    {
        return Crimes.find(Crimes.class, "Search_History=? and _Date=?", new String[]{String.valueOf(_searchHistoryID), _date.toString("Y-MM")});
    }

    /**
     * Gets response from http API call.
     * @return HTTP Status code.
     */
    public int get_responseCode()
    {
        return _responseCode;
    }

}
