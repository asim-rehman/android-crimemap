package uk.asimrehman.crimemap;



import android.graphics.Color;
import android.support.annotation.Nullable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.asimrehman.crimemap.Framework.HelperMethods;
import uk.asimrehman.crimemap.Framework.Permissions;
import uk.asimrehman.crimemap.Framework.ProgressBarView;
import uk.asimrehman.crimemap.Models.Crimes;
import uk.asimrehman.crimemap.Models.Location;
import uk.asimrehman.crimemap.Models.SearchHistory;
import uk.asimrehman.crimemap.businesslogiclayer.CrimesBLL;

import static android.os.Debug.startMethodTracing;


/**
 * Created by admin on 05/10/2016.
 */

public class CrimeMapFragment extends BaseFragment implements OnMapReadyCallback {

    //Set private variables

    private MapView _mapView;
    private GoogleMap _googleMap;

    private ProgressBarView _progressBarView = null;
    private FrameLayout _progressBarFrame = null;
    private SearchHistory _searchHistory;
    private HashMap<Integer, Long> _selectedCategories;


    public final static String DATE ="date";
    public final static String SEARCHHISTORY ="SearchHistoryID";

    private double _lat = 0;
    private double _lng = 0;
    private long _searchHistoryID =0;
    private String _selectedDate;
    private Boolean _isFromHistory;


    View _view;
    int responseCode;

    public CrimeMapFragment()
    {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstantState)
    {
        super.onCreateView(inflater,container,savedInstantState);
        //Inflate our view
        View view = getView();

        savedInstantState = getArguments();
        //Get our passed long and lat args, if they are null throw exception.
        if (savedInstantState.getString(DATE)!=null && savedInstantState.getLong(SEARCHHISTORY) != 0)
        {
            _selectedDate = savedInstantState.getString(DATE);
            _searchHistoryID = savedInstantState.getLong(SEARCHHISTORY);

            _searchHistory = SearchHistory.findById(SearchHistory.class, _searchHistoryID);
            _lat = _searchHistory.getLatitude();
            _lng = _searchHistory.getLongitude();

            if(savedInstantState.getSerializable(CategoriesFilterViewFragment.SELECTEDCATEGORIES)!=null)
            {
                _selectedCategories =(HashMap<Integer, Long>) savedInstantState.getSerializable(CategoriesFilterViewFragment.SELECTEDCATEGORIES);
            }
        }
        else
        {
            throw new IllegalArgumentException("The long and lat parameters were not supplied");
        }

        //Get ProgressBar (Hidden by default)
        _progressBarView = new ProgressBarView(view);
        _progressBarFrame = (FrameLayout)view.findViewById(R.id.include_progressbar);

        //Find our mapview
        _mapView = (MapView)view.findViewById(R.id.mapView);
        _mapView.onCreate(savedInstantState);


        _view=view;

        setTitle(R.string.fragment_crime_map_title);
        return view;
    }

    @Override
    protected int setResourceView() {
        return R.layout.fragment_crime_map;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        try {

            //Initialize the map
            new GetCrimeSearchHistory().execute();

        } catch (Exception e) {
            SnackBarMessage( getString(R.string.internal_error));
        }

    }


    @Override
    public void onResume()
    {
        super.onResume();
        _mapView.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.default_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.filter_menu:

                CategoriesFilterViewFragment categoriesFilterView = new CategoriesFilterViewFragment();
                Bundle args = new Bundle();
                args.putString(CategoriesFilterViewFragment.DATE, _selectedDate);
                args.putLong(CategoriesFilterViewFragment.SEARCHHISTORYID, _searchHistoryID);
                args.putSerializable(CategoriesFilterViewFragment.SELECTEDCATEGORIES, _selectedCategories);
                categoriesFilterView.setArguments(args);
                HelperMethods.ChangeFragment(getActivity(), categoriesFilterView);

            break;

        }

        return true;

    }



    //This method should only be called if the current location and crime data is not in database,
    //For the current month-year

    public void SetMapAsync()
    {
        _mapView.getMapAsync(this);
    }
    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onMapReady(GoogleMap mMap) {
        _googleMap = mMap;

        final Permissions permissions = new Permissions((AppCompatActivity)getActivity());

        if (permissions.HaveLocationPermissions())
        {
            _googleMap.setMyLocationEnabled(true);
            new onMapReadyPlotData().execute();
        }
        else
        {
            SnackBarMessage(getString(R.string.no_location));
        }
    }

    public class GetCrimeData extends AsyncTask<Void, String, List<Crimes>>
    {
        CrimesBLL crimeMapBLL;
        List<Crimes> crimesList;


        @Override
        protected  List<Crimes> doInBackground(Void... params) {

            crimeMapBLL = new CrimesBLL(_lat, _lng, _selectedDate, _searchHistoryID, this);
            /**
             * Always run this method in background, it will block UI thread and it makes a HTTP request
             * Android does not allow HTTP requests on UI thread.
             */
            crimesList = crimeMapBLL.GetCrimeData();
            crimeMapBLL.SaveCrimeData(crimesList);
            responseCode=crimeMapBLL.get_responseCode();

            return crimesList;
        }

        @Override
        protected void onPostExecute(List<Crimes> crimes) {
            super.onPostExecute(crimes);
            _progressBarFrame.setVisibility(View.GONE);


             if(crimes.size()>0)
             {
                 SetMapAsync();
             }
            else
             {
                 SnackBarMessage("Sorry no data for this month");
                 HelperMethods.ChangeFragment(getActivity(),new SearchLocationFragment());
             }

         }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressBarFrame.setVisibility(View.VISIBLE);
        }

        public void PublishProgress(String progress)
        {
            publishProgress(progress);
        }
    }

    class onMapReadyPlotData extends AsyncTask<List<Crimes>, String, List<Crimes>>
    {
        CameraPosition cameraPosition=null;
        LatLng loc=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            _progressBarFrame.setVisibility(View.VISIBLE);
            _progressBarView.SetProgressMessage("Populating Map");
        }

        @Override
        protected void onPostExecute(List<Crimes> allCrimes) {
            super.onPostExecute(allCrimes);
            setProgress("Populating Map...");

            for (int i=0; i< allCrimes.size();i++)
            {
                setProgress("Populating Map..." + i + " of " + allCrimes.size());
                Crimes crimes = allCrimes.get(i);
                Location lot = crimes.Location;
                loc = new LatLng(lot.get_latitude(), lot.get_longitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.title(crimes.Category.getName());

                String message = crimes.Location.get_streetName();

                markerOptions.snippet(message);



                int red = Color.red(crimes.Category.getColour());
                int green = Color.green(crimes.Category.getColour());
                int blue = Color.blue(crimes.Category.getColour());

                float[] hsv = new float[3];

                Color.RGBToHSV(red, green, blue, hsv);

                _googleMap.addMarker(markerOptions)
                        .setIcon(BitmapDescriptorFactory.defaultMarker(hsv[0]));

                _googleMap.setBuildingsEnabled(true);


            }

            if(loc!=null)
            {
                cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                if(cameraPosition!=null)
                    _googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            _progressBarFrame.setVisibility(View.GONE);

        }

        @Override
        protected List<Crimes> doInBackground(List<Crimes>... params) {

            List<Crimes> allCrimes;

            setProgress("Populating Map");
            if(_selectedCategories !=null)
            {
                ArrayList<Long> _categories = new ArrayList<>();
                for(Long value : _selectedCategories.values())
                {
                    _categories.add(value);
                }
                allCrimes = CrimesBLL.GetCrimesForCategories(_searchHistoryID, DateTime.parse(_selectedDate), _categories);
            }
            else
            {
                allCrimes = CrimesBLL.GetCrimesBySearchHistoryAndDate(_searchHistoryID, DateTime.parse(_selectedDate));
            }

            return allCrimes;
        }

        public void setProgress(String value)
        {
            publishProgress(value);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }
    }


    //This method can take a long time
    //so it is best to run it in separate thread
    class GetCrimeSearchHistory extends AsyncTask<Boolean, String, Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressBarFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(_isFromHistory)
            {
                _mapView.onStart();
                MapsInitializer.initialize(getActivity().getApplicationContext());
                SetMapAsync();
            }
            else
            {
                GetCrimeData getCrimeData = new GetCrimeData();
                getCrimeData.execute();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            publishProgress("Getting records");
              _isFromHistory = CrimesBLL.DoCrimesExistForSearchHistory(_searchHistoryID,DateTime.parse(_selectedDate));//This method causes UI blocks!
            return _isFromHistory;
        }

    }
}
