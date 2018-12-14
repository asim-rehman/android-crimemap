package uk.asimrehman.crimemap.Framework;


import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;

import uk.asimrehman.crimemap.R;


/**
 * Created by admin on 14/10/2016.
 */

public class ProgressBarView {


    public ProgressBar progressBar=null;
    public TextView progressStatus=null;

    public ProgressBarView(View view)
    {
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
        progressStatus = (TextView)view.findViewById(R.id.tvProgressStatus);
    }

    public ProgressBarView(AppCompatActivity appCompatActivity)
    {
        progressBar = (ProgressBar)appCompatActivity.findViewById(R.id.progressBar1);
        progressStatus = (TextView)appCompatActivity.findViewById(R.id.tvProgressStatus);
    }

    public void SetProgressMessage(String text)
    {
        progressStatus.setText(text);
    }


}
