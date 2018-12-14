package uk.asimrehman.crimemap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Test;


import java.util.List;

import uk.asimrehman.crimemap.Core.PoliceAPI;
import uk.asimrehman.crimemap.Models.Categories;
import uk.asimrehman.crimemap.Models.Crimes;

import static org.junit.Assert.assertEquals;


/**
 * Created by admin on 01/10/2016.
 */

public class PoliceAPIInstrumentedTest {

    String tag = "CRIMEMAPTEST";
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("uk.asimrehman.crimemap", appContext.getPackageName());
    }



    @Test
    public void testGetCategories()
    {
        PoliceAPI policeAPI = new PoliceAPI();
        List<Categories> crimesList = policeAPI.GetCategories();

        for(Categories categories : crimesList)
        {
            Log.d(tag, categories.getName() + "URL=" + categories.getUrl()) ;
        }

    }


    @Test
    public void testGetCrime()
    {
        PoliceAPI policeAPI = new PoliceAPI();
        List<Crimes> crimesList = policeAPI.GetCrimes(52.7814053d, -3.8056511d, "2013-01");

        for(Crimes crime : crimesList)
        {
             Log.d(tag, crime.get_outcome()) ;
        }

    }

    @Test
    public void testPoliceAPILastUpdated()
    {
        PoliceAPI policeAPI = new PoliceAPI();
        Log.d(tag, policeAPI.GetLastUpdated().toString()) ;
    }

}
