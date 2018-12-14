package uk.asimrehman.crimemap;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.List;

import uk.asimrehman.crimemap.Framework.Adapters.CategoriesAdapter;

import uk.asimrehman.crimemap.Models.Categories;
import uk.asimrehman.crimemap.businesslogiclayer.CategoriesBLL;


public class CrimeCategoriesFragment extends BaseFragment {


    private ListView _crimeCategoriesView;
    private CategoriesAdapter _categoriesListAdapter;
    private List<Categories> _categoriesList;
    CategoriesBLL categoriesBLL;

    public CrimeCategoriesFragment()
    {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View categoriesListView = getView();



        _crimeCategoriesView = (ListView)categoriesListView.findViewById(R.id.lvCrimeCategories);
        InitializeData();

        _crimeCategoriesView.setOnItemClickListener(new ColourButtonOnClickListener());

        setTitle(R.string.fragment_crime_categoreis_title);



        return categoriesListView;

    }

    @Override
    protected int setResourceView() {
        return R.layout.fragment_crime_categories;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public void InitializeData()
    {
        categoriesBLL = new CategoriesBLL();
        _categoriesList = categoriesBLL.ListAll();
        _categoriesListAdapter = new CategoriesAdapter(getActivity(), _categoriesList);
        _crimeCategoriesView.setAdapter(_categoriesListAdapter);
    }

    class ColourButtonOnClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {



            final Categories getClickedItem = categoriesBLL.findById(id);

            int red = 92;
            int green = 69;
            int blue = 221;

            if(getClickedItem!=null)
            {
                red = Color.red(getClickedItem.getColour());
                green = Color.green(getClickedItem.getColour());
                blue = Color.blue(getClickedItem.getColour());
            }

            final ColorPicker cp = new ColorPicker(getActivity(), red, green, blue);
            cp.show();

            Button okColourClick = (Button)cp.findViewById(R.id.okColorButton);
            final EditText hexCode = (EditText)cp.findViewById(R.id.codHex);
            hexCode.setEnabled(false);
            hexCode.setFocusable(false);

            okColourClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getClickedItem.setColour(cp.getColor());
                    getClickedItem.save();

                    cp.dismiss();

                    InitializeData();
                }
            });
        }
    }
}
