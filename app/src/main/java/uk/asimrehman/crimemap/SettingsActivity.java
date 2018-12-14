package uk.asimrehman.crimemap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;


import org.joda.time.DateTime;

import uk.asimrehman.crimemap.Framework.ProgressBarView;
import uk.asimrehman.crimemap.Framework.SettingsManager;
import uk.asimrehman.crimemap.Core.PoliceAPI;
import uk.asimrehman.crimemap.businesslogiclayer.CrimesBLL;
import uk.asimrehman.crimemap.businesslogiclayer.LocationBLL;
import uk.asimrehman.crimemap.businesslogiclayer.SearchHistoryBLL;

public class SettingsActivity extends BaseActivity {


    private AppCompatActivity _activity = this;
    RelativeLayout progressWrapper;
    SettingsManager settingsManager;

    TextView textViewCacheDescription;
    Switch switchCaching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout settingsRelativeLayoutOne = (RelativeLayout)findViewById(R.id.settingsRelativeLayoutOne);
        RelativeLayout settingsRelativeLayoutTwo = (RelativeLayout)findViewById(R.id.settingsRelativeLayoutTwo);
       // textViewCacheDescription = (TextView)findViewById(R.id.textViewCacheDescription);
        //switchCaching = (Switch)findViewById(R.id.switchCaching);

        progressWrapper = (RelativeLayout) findViewById(R.id.wrapperProgressBar);
        settingsManager = new SettingsManager(_activity);

       settingsRelativeLayoutOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new onUpdatePoliceCrimesDates().execute();
            }
        });

        settingsRelativeLayoutTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder confirmationDialogBuilder = new AlertDialog.Builder(_activity,android.R.style.Theme_Material_Light_Dialog_Alert);
                confirmationDialogBuilder.setTitle("Please confirm");
                confirmationDialogBuilder.setMessage("Are you sure?");

                confirmationDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new onClearSearchHistory().execute();
                    }
                }).setNegativeButton("Cancel", null);

                AlertDialog confirmationDialog = confirmationDialogBuilder.create();
                confirmationDialog.show();
            }
        });

/*        switchCaching.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = settingsManager.GetSharedPrefs().edit();
                editor.putBoolean(settingsManager.CacheEnabledKey,isChecked);
                settingsManager.UpdateSharedPreferences(editor);

                SetCacheInfo();

            }
        });*/

        if(getToolBar()!=null)
        {
            getToolBar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }

    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    protected void HomeClickAction()
    {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }


    class onUpdatePoliceCrimesDates extends AsyncTask<Void, String, Void>
    {
        private ProgressBarView _progressBarView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressBarView = new ProgressBarView(_activity);
                progressWrapper.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressWrapper.setVisibility(View.GONE);

            Snackbar.make(getSnackBar(),"Updated Successfully", Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            publishProgress("Connecting..");
            PoliceAPI policeAPI = new PoliceAPI();
            publishProgress("Updating...");
            DateTime getUpdate = policeAPI.GetLastUpdated();

            if(getUpdate!=null)
            {
                //do update..
                publishProgress("Updating.....");


                SharedPreferences.Editor editor = settingsManager.GetSharedPrefs().edit();
                editor.putString(settingsManager.PoliceAPILastUpdatedKey, getUpdate.toString());
                settingsManager.UpdateSharedPreferences(editor);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }
    }

    class onClearSearchHistory extends AsyncTask<Void, String, Void>
    {
        private ProgressBarView _progressBarView;
        private boolean _isSuccess=false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressBarView = new ProgressBarView(_activity);
            progressWrapper.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressWrapper.setVisibility(View.GONE);

            String text = getString(R.string.internal_error);

            if(_isSuccess)
                text = "Deleted Successfully";

            Snackbar.make(getSnackBar(), text, Snackbar.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try
            {
                publishProgress("Deleting Search History");
                new SearchHistoryBLL().DeleteAll();
                publishProgress("Deleting Locations");
                new LocationBLL().DeleteAll();
                publishProgress("Deleting Crimes");
                new CrimesBLL().DeleteAll();

                _isSuccess=true;
            }
            catch (Exception e)
            {
                _isSuccess=false;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }
    }

}
