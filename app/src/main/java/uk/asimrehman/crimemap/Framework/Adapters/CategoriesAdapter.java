package uk.asimrehman.crimemap.Framework.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import uk.asimrehman.crimemap.R;
import uk.asimrehman.crimemap.Models.Categories;

/**
 * Created by admin on 21/12/2016.
 */

public class CategoriesAdapter extends ArrayAdapter<Categories> {


    //Initialize private variables.
    private Context context;
    private List<Categories> categories;
    private LayoutInflater inflater;

    public CategoriesAdapter(Context context, List<Categories> objects) {
        super(context, 0, objects);
        this.context = context;
        this.categories = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View categoryLayoutView = inflater.inflate(R.layout.adapter_categories_listview, parent,false);


        TextView categoryName = (TextView)categoryLayoutView.findViewById(R.id.tvCrimeCategory);
        Button categoryColour = (Button)categoryLayoutView.findViewById(R.id.btnCategoryColour);


        //Get current category
        Categories getCurrent = getItem(position);
        
        categoryName.setText(getCurrent.getName());
        categoryColour.setBackgroundColor(getCurrent.getColour());

        return categoryLayoutView;

    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Nullable
    @Override
    public Categories getItem(int position) {
        return categories.get(position);    }

    @Override
    public int getPosition(Categories item) {
        return categories.indexOf(item);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        super.setNotifyOnChange(notifyOnChange);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
