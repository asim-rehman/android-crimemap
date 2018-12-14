package uk.asimrehman.crimemap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



/**
 * Created by admin on 25/12/2016.
 */

/***
 * BaseFragment used for other fragments
 */
public abstract class BaseFragment extends Fragment {

    private View view;
    protected abstract int setResourceView();
    public BaseFragment()
    {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(setResourceView(),container, false);

        return view;
    }


    public View getView(){
        return view;
    }



    public void setTitle(int resource)
    {
        getActivity().setTitle(resource);
    }

    public void SnackBarMessage(String message)
    {
        if(view!=null)
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public void SnackBarMessage(int message)
    {
        if(view!=null)
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public void MakeToast(String message)
    {
        Toast.makeText(getActivity(), message,Toast.LENGTH_LONG).show();
    }



}
