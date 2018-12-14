package uk.asimrehman.crimemap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;


import org.junit.Test;

import uk.asimrehman.crimemap.businesslogiclayer.CrimesBLL;

import static org.junit.Assert.assertEquals;

/**
 * Created by admin on 07/01/2017.
 */

public class CrimesInstrumentedTest {
    String tag = "CRIMEMAPTEST";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("uk.asimrehman.crimemap", appContext.getPackageName());
    }


    @Test
    public void testGetCrime()
    {
        CrimesBLL crimesBLL = new CrimesBLL(52.7814053d,-3.8056511d,"2013-01",1,null);
        //boolean success =  crimesBLL.GetCrimeData();

       /// Log.d(tag, String.valueOf(success));

    }

}
