package uk.asimrehman.crimemap;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import uk.asimrehman.crimemap.Framework.HelperMethods;

/**
 * Created by admin on 30/12/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar _myToolbar;
    private View SnackBar;
    protected abstract int getLayoutResource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        SnackBar = findViewById(R.id.snackbarPosition);

        initializeToolBar();
        setScreenOrientation();
    }

    void initializeToolBar()
    {
        _myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(_myToolbar);

        if(_myToolbar!=null)
            _myToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    protected View getSnackBar()
    {
        return SnackBar;
    }

    protected Toolbar getToolBar()
    {
        return _myToolbar;
    }

    public void setScreenOrientation()
    {

        if(HelperMethods.isTablet(this))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                HomeClickAction();
                break;
        }

        return false;
    }

    protected abstract void HomeClickAction();


}
