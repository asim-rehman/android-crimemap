package uk.asimrehman.crimemap.Framework;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 24/12/2016.
 */

public class Permissions {

    AppCompatActivity appCompatActivity;

    final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    public Permissions(AppCompatActivity appCompatActivity)
    {
        this.appCompatActivity =appCompatActivity;
    }


    /***
     * Check if we have a valid internet connection
     * Internet Connection is required to do Address and Crime Lookup
     * @return True if we have an internet and we can connect to Google, else false
     */
    public Boolean IsInternetEnabled()
    {
        ConnectivityManager cm =  (ConnectivityManager)appCompatActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    public Boolean HaveLocationPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(appCompatActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        return false;
    }


    public void RequestLocationPermissions()
    {

            if(ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                ShowLocationDialogMessage();
            }
            else
            {
                ActivityCompat.requestPermissions(appCompatActivity,permissions,MY_PERMISSIONS_REQUEST_LOCATION);
            }
    }



    public void ShowLocationDialogMessage()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(appCompatActivity);
        alertDialog.setTitle("Location Permissions");
        alertDialog.setMessage("Location permissions are required to use this application");
        alertDialog.setPositiveButton("Retry", new PermissionsRetryOnClick());
        alertDialog.setNegativeButton("Exit", new PermissionsExitOnClick());
        alertDialog.create().show();
    }

    class PermissionsRetryOnClick implements AlertDialog.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ActivityCompat.requestPermissions(appCompatActivity,permissions,MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    class PermissionsExitOnClick implements AlertDialog.OnClickListener
    {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            System.exit(0);
        }
    }
}
