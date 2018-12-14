package uk.asimrehman.crimemap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import uk.asimrehman.crimemap.Framework.HelperMethods;
import uk.asimrehman.crimemap.Models.Categories;
import uk.asimrehman.crimemap.businesslogiclayer.CategoriesBLL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFilterViewFragment extends BaseFragment {


    public static final String DATE ="date";
    public static final String SEARCHHISTORYID="SearchHistoryID";
    public static final String SELECTEDCATEGORIES="SELECTEDCATEGORIES";

    //private ArrayList<Integer> _selectedCategories = new ArrayList<>();
    private ListView _categoriesListView;
    private List<Categories> _categoriesList;
    private View _view;
    private ArrayAdapter _arrayAdapter;

    private String _selectedDate;
    private long _searchHistoryID;
    private HashMap<Integer, Long> _selectedCategories = new HashMap<>();

    public CategoriesFilterViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        _view = getView();

        savedInstanceState = getArguments();
        _selectedDate = savedInstanceState.getString(DATE);
        _searchHistoryID = savedInstanceState.getLong(SEARCHHISTORYID);


        if(savedInstanceState.getSerializable(SELECTEDCATEGORIES)!=null)
            _selectedCategories =(HashMap<Integer, Long>)savedInstanceState.getSerializable(SELECTEDCATEGORIES);

        initializeWidgets();

        SetCheckedCategories();

        setTitle(R.string.fragment_categories_filter_title);
        return _view;
    }

    @Override
    protected int setResourceView() {
        return R.layout.fragment_categories_filter_view;
    }

    void initializeWidgets()
    {
        _categoriesList = CategoriesBLL.GetCategoriesForSearchHistoryAndDate(_searchHistoryID, DateTime.parse(_selectedDate));
        _arrayAdapter = new ArrayAdapter(getActivity(),R.layout.adapter_simple_list_item_checked, _categoriesList);


        _categoriesListView = (ListView)_view.findViewById(R.id.listViewCategoriesFilter);
        _categoriesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        _categoriesListView.setAdapter(_arrayAdapter);

        _categoriesListView.setOnItemClickListener(new CategoriesOnClick());
    }

    class CategoriesOnClick implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isChecked = ((CheckedTextView)view).isChecked();
            if(isChecked)
            {
                _selectedCategories.put(position, _categoriesList.get(position).getId());
            }
            else
            {
                _selectedCategories.remove(position);
            }
        }
    }


    public void SetCheckedCategories()
    {
        if(_selectedCategories.size()==0)
        {
            for(int i=0; i < _categoriesList.size();i++)
            {
                _selectedCategories.put(i,_categoriesList.get(i).getId());
            }
        }

        for (Integer pos : _selectedCategories.keySet())
        {
            _categoriesListView.setItemChecked(pos, true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.category_filter_options,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.filter_ok:
                if(_selectedCategories.size() == 0)
                {
                    SnackBarMessage("Please choose at least one category");
                }
                else
                {
                    CrimeMapFragment crimeMap = new CrimeMapFragment();
                    HelperMethods.SetCrimeMapBundleArgs(crimeMap, _selectedDate, _searchHistoryID, _selectedCategories);
                    HelperMethods.ChangeFragment(getActivity(),crimeMap);
                }

                break;

            case R.id.filter_clear:
                CrimeMapFragment crimeMap = new CrimeMapFragment();
                HelperMethods.SetCrimeMapBundleArgs(crimeMap, _selectedDate, _searchHistoryID,_selectedCategories);
                HelperMethods.ChangeFragment(getActivity(),crimeMap);
                break;
        }
        return true;
    }

}
