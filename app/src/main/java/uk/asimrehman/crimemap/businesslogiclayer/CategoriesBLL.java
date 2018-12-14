package uk.asimrehman.crimemap.businesslogiclayer;


import org.joda.time.DateTime;
import java.util.List;

import uk.asimrehman.crimemap.Models.Categories;

/**
 * Created by admin on 23/12/2016.
 */

/**
 * Class for modifying and retrieving categories
 */
public class CategoriesBLL extends GenericBLL<Categories> {

    /**
     *
     */
    public CategoriesBLL()
    {
        super(Categories.class);
    }

    /**
     * Retrieve categories for a given search history
     * @param _searchHistoryID The ID of the search history to get categories for
     * @param _date The date to get categories from
     * @return Returns a list of categories
     */
    public static List<Categories> GetCategoriesForSearchHistoryAndDate(long _searchHistoryID, DateTime _date)
    {

        String query = "SELECT DISTINCT Categories.Name, Categories.ID, Categories.URL From Crimes " +
                "INNER JOIN Categories ON Crimes.Category = Categories.ID Where Search_History=? AND _date=?";

        List<Categories> categories = Categories.findWithQuery(Categories.class, query, String.valueOf(_searchHistoryID), _date.toString("Y-MM"));

        return categories;
    }

}
