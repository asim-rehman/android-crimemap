package uk.asimrehman.crimemap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.view.Gravity;


import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import uk.asimrehman.crimemap.Framework.HelperMethods;
import uk.asimrehman.crimemap.Framework.Permissions;


public class MainActivity extends BaseActivity {

    private String[] _menuItems;
    private DrawerLayout _drawerLayout;
    private ListView _listView;
    private ArrayAdapter<String> _navigationItems;
    private AppCompatActivity _activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        initializeNavigationDrawer();

        //Change fragment
        SearchLocationFragment slFragment = new SearchLocationFragment();
        HelperMethods.ChangeFragment(_activity, slFragment);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    void initializeNavigationDrawer()
    {
        _menuItems = getResources().getStringArray(R.array.NavigationDrawerMenuItems);
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        _listView = (ListView) findViewById(R.id.left_drawer);

        _navigationItems = new ArrayAdapter<>(this, R.layout.adapter_drawer_list_item, _menuItems);
        _listView.setAdapter(_navigationItems);
        _listView.setOnItemClickListener(new DrawerItemListener());
        _drawerLayout.addDrawerListener(new NavigationDrawerListener());
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, String permissions[], int[] grantResults)
    {
        switch (resultCode)
        {
            case 1:
            {
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0] );
                    if(!showRationale)
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        alertDialog.setTitle(R.string.location_permissions);
                        alertDialog.setMessage(R.string.location_permission_denied);
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                System.exit(0);
                            }
                        });
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        });
                        alertDialog.create().show();
                    }
                    else
                    {
                        new Permissions(this).ShowLocationDialogMessage();
                    }
                }
            }
        }
    }
    /***
     * Open close drawer navigation.
     * @param item
     * @return
     */


    @Override
    protected void HomeClickAction()
    {
        if (!_drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            _drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            _drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }



    /***
     * Class controls when drawer listener item is clicked.
     */
    class DrawerItemListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position, parent);
        }

        public void selectItem(int position, AdapterView parent) {


            String value = (String) _listView.getItemAtPosition(position);

            switch (value)
            {
                case "Crime Categories":

                    CrimeCategoriesFragment crimeCategoriesFragment = new CrimeCategoriesFragment();
                    HelperMethods.ChangeFragment(_activity, crimeCategoriesFragment);

                    break;

                case "Search Crimes":
                    SearchLocationFragment searchLocation = new SearchLocationFragment();
                    HelperMethods.ChangeFragment(_activity, searchLocation);
                    break;

                case "Settings":
                    Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(settingsActivity);
                    break;
            }

            _drawerLayout.closeDrawer(_listView);
        }
    }
    /**
     * Detect when drawer is opened/closed. Change the icons on the toolbar
     */
    class NavigationDrawerListener implements DrawerLayout.DrawerListener {

        Drawable arrowIcon = ContextCompat.getDrawable(_activity, R.drawable.ic_arrow_back_white_24dp);
        Drawable menuIcon =ContextCompat.getDrawable(_activity, R.drawable.ic_menu_white_24dp);

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            //Toast.makeText(getBaseContext(), "Drawer Opened", Toast.LENGTH_SHORT).show();
            getToolBar().setNavigationIcon(arrowIcon);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            //Toast.makeText(getBaseContext(), "Drawer Closed", Toast.LENGTH_SHORT).show();
            getToolBar().setNavigationIcon(menuIcon);

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }


}


