package uk.asimrehman.crimemap.businesslogiclayer;

import java.util.List;

import uk.asimrehman.crimemap.Models.SearchHistory;

/**
 * Created by admin on 19/10/2016.
 * Adds or gets new search history data
 */

public class SearchHistoryBLL extends GenericBLL<SearchHistory> {

    public SearchHistoryBLL()
    {
        super(SearchHistory.class);
    }

    /**
     * Get previously search areas
     * @param lat The latitude of location
     * @param lng The longitude of locaiton
     * @return SearchHistory model
     */
    public static SearchHistory GetHistory(Double lat, Double lng)
    {
        List<SearchHistory> getHistory = SearchHistory.find(SearchHistory.class, "latitude = ? and longitude = ?", lat.toString(), lng.toString());

        if(getHistory.size()>0)
            return getHistory.get(0);
        else
            return null;
    }

    /**
     * Get search history by ID
     * @param id ID of search history
     * @return SearchHistory model
     */
    public static  SearchHistory GetById(long id)
    {
        return  SearchHistory.findById(SearchHistory.class, id);
    }


}
