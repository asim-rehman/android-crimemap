package uk.asimrehman.crimemap.Framework;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.location.Address;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import uk.asimrehman.crimemap.CategoriesFilterViewFragment;
import uk.asimrehman.crimemap.CrimeMapFragment;
import uk.asimrehman.crimemap.R;

/**
 * Created by admin on 13/10/2016.
 */

public class HelperMethods {


    public static void ChangeFragment(FragmentActivity activity, Fragment fragment)
    {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.addToBackStack(null);
        ft.replace(R.id.mainContent, fragment);

        ft.commit();
    }

    public static String getAddressLine(Address address)
    {
        String returnValue="";
        for(int i=0;i<address.getMaxAddressLineIndex();i++)
        {
            if (i != address.getMaxAddressLineIndex())
                returnValue=returnValue + address.getAddressLine(i) + ", ";
            else
                returnValue=returnValue + address.getAddressLine(i);
        }

        return returnValue;
    }

    public static void SetCrimeMapBundleArgs(CrimeMapFragment crimeMap, String Date, long SearchHistoryID, HashMap<Integer,Long> categories)
    {
        Bundle args = new Bundle();
        args.putString(crimeMap.DATE,Date);
        args.putLong(crimeMap.SEARCHHISTORY, SearchHistoryID);

        if(categories!=null)
        {
            if(categories.size()>0)
                args.putSerializable(CategoriesFilterViewFragment.SELECTEDCATEGORIES,categories);
        }


        crimeMap.setArguments(args);
    }

    public static boolean isTablet(AppCompatActivity appCompatActivity)
    {
        return appCompatActivity.getResources().getBoolean(R.bool.isTablet);
    }

}
