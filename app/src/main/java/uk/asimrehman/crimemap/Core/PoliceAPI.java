package uk.asimrehman.crimemap.Core;


import android.util.Log;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import uk.asimrehman.crimemap.CrimeMapFragment;
import uk.asimrehman.crimemap.Models.Categories;
import uk.asimrehman.crimemap.Models.Crimes;
import uk.asimrehman.crimemap.Models.Location;

/**
 * Created by admin on 19/12/2016.
 * API used to retrieve data from Police.UK website
 */

public class PoliceAPI {


    CrimeMapFragment.GetCrimeData crimeData;
    int _responseCode;

    public PoliceAPI()
    {

    }

    public PoliceAPI(CrimeMapFragment.GetCrimeData crimeData)
    {
        this.crimeData = crimeData;
    }

    /**
     * Get crimes for given area from Police.UK API. This method makes HTTP request always run in background thread
     * Android will not let you run it on UI thread, it causes blocks
     * @param lat Latitude of location
     * @param lng Longitude of location
     * @param date Date of location
     * @return List of crimes model
     */
    public List<Crimes> GetCrimes(Double lat, Double lng, String date)
    {

        String requestedURL = "https://data.police.uk/api/crimes-street/all-crime?lat="+lat.toString()+"&lng="+lng.toString()+
                "&date="+date;

        JSONArray jsondata  = MakeJsonRequest(requestedURL);
        List<Crimes> crimesList = new ArrayList<>();

        if(jsondata != null)
        {
            try
            {
                PublishProgress("Reading Records ");
                for (int i=0; i < jsondata.length();i++)
                {

                    PublishProgress(String.valueOf("Processing Record " + i + " of " + jsondata.length()));
                    JSONObject crimeObject = (JSONObject)jsondata.get(i);
                    Crimes crime = new Crimes();
                    Location location = new Location();

                    if (!crimeObject.isNull("outcome_status"))
                    {
                        JSONObject outComeStatus = crimeObject.getJSONObject("outcome_status");

                        if(outComeStatus!=null)
                        {
                            if(outComeStatus.getString("category") != null)
                            {
                                crime.set_outcome(outComeStatus.getString("category"));
                            }
                        }

                    }

                    if (!crimeObject.isNull("persistent_id"))
                        crime.set_persistendid(crimeObject.getString("persistent_id"));

                    crime.set_crimeid(crimeObject.getInt("id"));
                    crime.set_Date(date);

                    //Location Data
                    JSONObject locationObject = crimeObject.getJSONObject("location");
                    Double crimelat = locationObject.getDouble("latitude");
                    Double crimelng = locationObject.getDouble("longitude");

                    //Street Data
                    JSONObject streetObject = locationObject.getJSONObject("street");

                    location.set_latitude(crimelat);
                    location.set_longitude(crimelng);
                    location.set_streetName(streetObject.getString("name"));
                    location.set_streetid(streetObject.getInt("id"));

                    crime.Location = location;

                    List<Categories> getcategory = Categories.find(Categories.class, "url = ?", crimeObject.getString("category"));
                    if (getcategory.size() >0)
                    {
                        Categories categories = getcategory.get(0);
                        crime.Category = categories;
                    }


                    crimesList.add(crime);
                }
            }
            catch (Exception e)
            {

            }
        }

        return crimesList;
    }

    /**
     * Get the last time Police.UK updated their records
     * (It's usually one or two months behind)
     * @return DateTime of new update.
     */
    public DateTime GetLastUpdated() {

        String url = "https://data.police.uk/api/crime-last-updated";

        JSONObject getRequestResponse = MakeJsonObjectRequest(url);

        if(getRequestResponse!=null)
        {
            try
            {
                    if(getRequestResponse.getString("date")!=null)
                    {
                        return DateTime.parse(getRequestResponse.getString("date"));
                    }
            }
            catch (Exception e)
            {

            }

        }

        return null;
    }

    /**
     * Gets categories of crime from Police.UK
     * @return list of categories
     */
    public List<Categories> GetCategories()
    {
        String url = "https://data.police.uk/api/crime-categories";

        JSONArray getJsonFromRequest = MakeJsonRequest(url);

        List<Categories> CategoriesList = new ArrayList<>();

        if(getJsonFromRequest!=null)
        {
            try {
                for(int i=0;i<getJsonFromRequest.length();i++)
                {
                    Categories categories = new Categories();
                    JSONObject currentObject = (JSONObject)getJsonFromRequest.get(i);

                    if(currentObject!=null)
                    {
                        String urlname = currentObject.getString("url");
                        String name = currentObject.getString("name");

                        if(!urlname.equals("all-crime"))
                        {
                            categories.setUrl(urlname);
                            categories.setName(name);

                            CategoriesList.add(categories);
                        }
                    }

                }
            }
            catch (Exception e)
            {

            }

        }

        return CategoriesList;
    }


    String GetDataFromConnection(String link) throws IOException
    {

            PublishProgress("Connecting...");
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            _responseCode = conn.getResponseCode();
            InputStream content = new BufferedInputStream(conn.getInputStream());
            PublishProgress("Gathering content....");

            String json = "";
            if(_responseCode==HttpURLConnection.HTTP_OK)
            {
                json =  ConvertInputStreamToString(content);
            }



            conn.disconnect();

            return json;
    }

    JSONObject MakeJsonObjectRequest(String link)
    {
        try
        {
            String json = GetDataFromConnection(link);
            JSONObject jsonObject = new JSONObject(json);

            return jsonObject;
        }
        catch (Exception e)
        {
            Log.d("Error:", e.getMessage());
        }

        return  null;
    }

    JSONArray MakeJsonRequest(String link)
    {
        try
        {
            String json = GetDataFromConnection(link);
            JSONArray jsonArray = new JSONArray(json);

            return jsonArray;
        }
        catch (Exception e)
        {
            Log.d("Error:", e.getMessage());
        }

        return  null;
    }


    private String ConvertInputStreamToString(InputStream content)
    {
        PublishProgress("Processing information");
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        StringBuilder sb = new StringBuilder();

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
                PublishProgress("Processing information.");
                PublishProgress("Processing information..");
                PublishProgress("Processing information...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                content.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    void PublishProgress(String message)
    {
        if(crimeData!=null)
        {
            crimeData.PublishProgress(message);
        }
    }

    /**
     * Get http response code
     * @return HTTP response code
     */
    public int get_responseCode() {
        return _responseCode;
    }

}
