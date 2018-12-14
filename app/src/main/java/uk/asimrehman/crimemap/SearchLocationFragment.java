package uk.asimrehman.crimemap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.asimrehman.crimemap.Framework.Adapters.AddressAdapter;
import uk.asimrehman.crimemap.Framework.HelperMethods;
import uk.asimrehman.crimemap.Framework.Permissions;
import uk.asimrehman.crimemap.Framework.ProgressBarView;
import uk.asimrehman.crimemap.Framework.SettingsManager;
import uk.asimrehman.crimemap.Models.Categories;
import uk.asimrehman.crimemap.Models.SearchHistory;
import uk.asimrehman.crimemap.businesslogiclayer.CategoriesBLL;
import uk.asimrehman.crimemap.Core.PoliceAPI;
import uk.asimrehman.crimemap.businesslogiclayer.SearchHistoryBLL;

import static android.os.Debug.startMethodTracing;


public class SearchLocationFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    //Initialize private variables.
    private AutoCompleteTextView _autocompleteSearchLocation;
    private ListView _addressList;
    private AlertDialog alertDialog;

    private ProgressBarView _progressBarView;
    private View _progressBarInclude;
    private EditText _editTextCrimeDate;
    private Button _btnSearchAddresses;
    private Geocoder _geocoder;

    private GoogleApiClient _googleApiClient;
    private Permissions _permissions;
    private View _view;


    private Boolean _isAddressFromHistory =false;
    private long _searchHistoryID;
    private int _selectedAddressPosition =0;
    private String _selectedDate;

    DatePickerDialog.OnDateSetListener dateListener;
    CategoriesBLL categoriesBLL;

    public SearchLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup Google gecoder
        _geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.UK);
        // Create an instance of GoogleAPIClient.
        if (_googleApiClient == null) {
            _googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        _permissions = new Permissions((AppCompatActivity)getActivity());
        setHasOptionsMenu(true);

        categoriesBLL = new CategoriesBLL();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        _view =  getView();
        initializeWidgets();


        setTitle(R.string.fragment_search_title);
        return _view;
    }

    @Override
    protected int setResourceView() {
        return R.layout.fragment_search_location;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        //Go get categories on first time
        //and last time system was updated.
        if(!_permissions.IsInternetEnabled())
        {
            _btnSearchAddresses.setEnabled(false);
            ShowSnackBarInternetWarning();
        }
        else
        {
            if(categoriesBLL.Count()<=0)
            {
                new onFirstTimeSetup().execute();
            }
        }
        //Ensure that we have _autocompleteSearchLocation permissions.
        Boolean result = _permissions.HaveLocationPermissions();

        if(!result)
        {
            _permissions.RequestLocationPermissions();
        }

    }

    void initializeWidgets()
    {
        //Get the search button
        _btnSearchAddresses = (Button)_view.findViewById(R.id.btnLocationSearch);
        //Set onclick listener for search button
        _btnSearchAddresses.setOnClickListener(new onAddressSearchClick());

        //Get the text box field.
        _autocompleteSearchLocation = (AutoCompleteTextView)_view.findViewById(R.id.etLocationSearch);
        _autocompleteSearchLocation.setOnEditorActionListener(new AutoCompleteEditorListener());
        SetLocationAdapter();

        _editTextCrimeDate = (EditText)_view.findViewById(R.id.editTextCrimeDate);
        setupDateTimeDialog();

        //Get ProgressBar (Hidden by default)
        _progressBarView = new ProgressBarView(_view);
        _progressBarInclude = _view.findViewById(R.id.include_progressbar);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_location_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.mylocation:
                _googleApiClient.connect();
            break;
        }


        return true;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new onGetCurrentLocation().execute();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This method gets the search history from the database,
     * it is populated as a
     */
    public void SetLocationAdapter()
    {
        if (_autocompleteSearchLocation != null)
        {

            final List<SearchHistory> searchHistoryList = new SearchHistoryBLL().ListAll();

            if (searchHistoryList.size()>0)
            {

                final ArrayAdapter<SearchHistory> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, searchHistoryList);
                _autocompleteSearchLocation.setAdapter(arrayAdapter);


                _autocompleteSearchLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        _isAddressFromHistory =true;
                        _searchHistoryID =arrayAdapter.getItem(0).getId();
                    }
                });
            }
        }
    }

    private void setupDateTimeDialog() {
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                monthOfYear = monthOfYear+1;
                if (monthOfYear <= 9)
                {
                    _selectedDate = year + "-0" + monthOfYear;
                }
                else
                {
                    _selectedDate = year + "-" + monthOfYear;
                }

                _editTextCrimeDate.setText(_selectedDate);

            }
        };

        _editTextCrimeDate.setOnClickListener(new DateOnClickListener());

    }



    void ShowSnackBarInternetWarning()
    {
        Snackbar snackbar = Snackbar.make(_view, R.string.NoInternet, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!_permissions.IsInternetEnabled())
                        {
                            ShowSnackBarInternetWarning();
                        }
                        else
                        {
                            _btnSearchAddresses.setEnabled(true);
                            if(categoriesBLL.Count() <=0)
                            {
                                new onFirstTimeSetup().execute();
                            }
                        }

                    }
                });

        snackbar.show();
    }



    class onFirstTimeSetup extends AsyncTask<Void, String, Void>
    {

        public onFirstTimeSetup() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _btnSearchAddresses.setVisibility(View.GONE);
            _progressBarInclude.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _btnSearchAddresses.setVisibility(View.VISIBLE);
            _progressBarInclude.setVisibility(View.GONE);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {

            publishProgress("Performing first time setup...Please wait.");
            PoliceAPI policeAPI = new PoliceAPI();
            List<Categories> categories = policeAPI.GetCategories();

            for(int i=0;i<categories.size();i++)
            {
                publishProgress("Performing first time setup...Please wait. " + i + "/" + categories.size());
                categories.get(i).setColour(Color.RED);
                categories.get(i).save();
            }

            publishProgress("Checking for updates...");
            DateTime lastUpdated = policeAPI.GetLastUpdated();

            if(lastUpdated!=null)
            {
                Log.d("CRIMEMAP", lastUpdated.toString());

                SettingsManager settingsManager = new SettingsManager((AppCompatActivity)getActivity());

                SharedPreferences.Editor editor = settingsManager.GetSharedPrefs().edit();
                editor.putString(settingsManager.PoliceAPILastUpdatedKey, lastUpdated.toString());
                editor.putBoolean(settingsManager.CacheEnabledKey,false);
                settingsManager.UpdateSharedPreferences(editor);
            }

            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

    class onAddressSearchClick implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

             //Execute our async task
            //This task can take a little while connecting to Google Play Services API
            //So it's method to use Async method and avoid blocking UI.
            if(!TextUtils.isEmpty(_editTextCrimeDate.getText().toString()) && !TextUtils.isEmpty(_autocompleteSearchLocation.getText().toString()))
            {
                String pattern = "[A-Za-z0-9, ]*";
                Pattern pattern1 = Pattern.compile(pattern);
                Matcher match = pattern1.matcher(_autocompleteSearchLocation.getText().toString());

                if(match.matches())
                {

                    if(_isAddressFromHistory)
                    {
                        CrimeMapFragment crimeMap = new CrimeMapFragment();
                        SearchHistory searchHistory = new SearchHistoryBLL().findById(_searchHistoryID);
                        HelperMethods.SetCrimeMapBundleArgs(crimeMap, _selectedDate, searchHistory.getId(),null);

                        HelperMethods.ChangeFragment(getActivity(), crimeMap);
                    }
                    else
                    {
                        onGetAddressLists downloadAddressAsync = new onGetAddressLists();
                        downloadAddressAsync.execute(_autocompleteSearchLocation.getText().toString());
                    }
                }
                else
                {
                    SnackBarMessage("Illegal characters in postcode");
                }


            }
            else
            {
                SnackBarMessage("Please fill out the fields");
            }
        }

    }

    class onGetCurrentLocation extends AsyncTask<Void, String, Void>
    {

        String postalCode="";
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _autocompleteSearchLocation.setText(postalCode);
            _progressBarInclude.setVisibility(View.GONE);
            _googleApiClient.disconnect();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressBarInclude.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressBarView.SetProgressMessage(values[0]);
        }

        @Override
        @SuppressWarnings({"MissingPermission"})
        protected Void doInBackground(Void... params) {
            publishProgress("Getting location");
            try
            {
                publishProgress("Checking permissions");
                if(_permissions.HaveLocationPermissions())
                {
                    publishProgress("Performing lookup");
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
                    if(lastLocation!=null)
                    {
                        publishProgress("Getting address");
                        List<Address> currentAddress = _geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);

                        if(currentAddress.size()>0)
                        {
                            publishProgress("Found address!");
                            postalCode=currentAddress.get(0).getPostalCode();
                        }
                    }
                    else
                    {
                        SnackBarMessage(R.string.no_location);
                    }
                }
                else{
                    _permissions.RequestLocationPermissions();
                }
            }
            catch (Exception e)
            {
                SnackBarMessage(R.string.internal_error);
            }
            return null;
        }
    }

    class onGetAddressLists extends AsyncTask<String, String, List<android.location.Address>>
    {
       @Override
        protected List<android.location.Address> doInBackground(String... params) {

            try
            {
                //Publish or current progress and set the status text
                publishProgress("Please Wait...");
                //Get our addresses

                //return addresses
                List<android.location.Address> addressList =  _geocoder.getFromLocationName(params[0], 10);

                return addressList;
            }
            catch (Exception e)
            {
                SnackBarMessage(R.string.internal_error);
            }

            //if something else, return null
            return null;

        }

        @Override
        protected void onPreExecute() {
            _progressBarInclude.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<android.location.Address> addresses) {

            try
            {
                //Hide the progressbar.
                _progressBarInclude.setVisibility(View.GONE);

                //Ensure addresses param is not null
                if (addresses != null)
                {

                    List<android.location.Address> addressList = addresses;
                    //ensure addresses param size is larger than 0
                    if (addressList.size() > 0)
                    {

                        AddressAdapter addressAdapter = new AddressAdapter(getActivity(), addressList);
                        
                        AlertDialog.Builder addressDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
                        
                        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                        View addressListView = layoutInflater.inflate(R.layout.adapter_address_listview,null);
                        addressDialog.setTitle(R.string.dialog_select_address_title);
                        addressDialog.setView(addressListView);
                        addressDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.cancel();
                            }
                        });
                        addressDialog.setPositiveButton("Select", new SelectOnClick());


                        _addressList = (ListView)addressListView.findViewById(R.id.lvAddress);
                        _addressList.setAdapter(addressAdapter);


                        _addressList.setOnItemClickListener(new AddressListOnItemClick());

                        alertDialog = addressDialog.create();
                        alertDialog.show();
                    }
                    else
                    {
                        SnackBarMessage(R.string.no_addresses_found);
                    }

                }
                else
                {
                    SnackBarMessage(R.string.internal_error);
                }
            }
            catch (Exception e)
            {
                SnackBarMessage(R.string.internal_error);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            _progressBarView.SetProgressMessage(values[0]);
        }
    }

    /**
     * This class handles the address clicks, when we show the dialog for our
     * returned addresses.
     */
    class AddressListOnItemClick implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //we need to get the clicked items position.
            _selectedAddressPosition =position;
        }
    }
    /**
     * This class handles the alert dialogs Positive button click.
     */
    class SelectOnClick implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {

            //Ensure list view is not null, if it is something has gone wrong
            //in doInBackground() method
            if (_addressList != null)
            {
                //Get our selected address.
                Address getSelectedAddress = (Address) _addressList.getItemAtPosition(_selectedAddressPosition);

                if (getSelectedAddress != null)
                {
                    SearchHistory searchHistory = new SearchHistory(getSelectedAddress);
                    SearchHistory exists = SearchHistoryBLL.GetHistory(getSelectedAddress.getLatitude(), getSelectedAddress.getLongitude());

                    if(exists != null)
                    {
                        _searchHistoryID = exists.getId();
                    }
                    else
                    {
                        _searchHistoryID = searchHistory.save();
                    }


                    CrimeMapFragment crimeMap = new CrimeMapFragment();
                    HelperMethods.SetCrimeMapBundleArgs(crimeMap, _selectedDate, _searchHistoryID,null);
                    HelperMethods.ChangeFragment(getActivity(),crimeMap);
                }
                else
                {
                    //The user has not selected an address.
                    SnackBarMessage(R.string.select_address);
                }
            }
        }
    }

    class AutoCompleteEditorListener implements TextView.OnEditorActionListener
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(actionId == EditorInfo.IME_ACTION_NEXT)
            {
                HideKeyboard();

                if(TextUtils.isEmpty(_editTextCrimeDate.getText().toString()))
                    new DateOnClickListener().onClick(_view);
                    else
                return true;
            }

            return false;
        }
    }

    class DateOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {

            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            String date = sharedPreferences.getString(getString(R.string.LastUpdated), new DateTime().toString());

            DateTime parseDate = DateTime.parse(date);
            int year= parseDate.getYear();
            int month = parseDate.getMonthOfYear();
            int day = parseDate.getDayOfMonth();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Material_Light_Dialog_Alert, dateListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(new DateTime(2010, 12, 1,0,0).getMillis());
            datePickerDialog.getDatePicker().setMaxDate(DateTime.parse(date).getMillis());

            datePickerDialog.setTitle("");
            datePickerDialog.show();
        }
    }

    void HideKeyboard()
    {
        if (_view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(_view.getWindowToken(), 0);
        }
    }

}
