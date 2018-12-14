package uk.asimrehman.crimemap.businesslogiclayer;

import com.orm.SugarRecord;

import java.util.List;


/**
 * Created by admin on 04/01/2017.
 * Generic class for retrieving data, uses SugarRecord ORM to get data.
 */

public class GenericBLL<T extends SugarRecord> {


    Class type;

    public GenericBLL(Class type)
    {
        this.type=type;
    }

    public List<T> ListAll()
    {
        return T.listAll(type);
    }

    public int DeleteAll()
    {
        return T.deleteAll(type);
    }

    public long Count()
    {
        return  T.count(type);
    }

    public T findById(long id)
    {
        return (T)T.findById(type,id);
    }

    public T findById(int id)
    {
        return (T)T.findById(type,id);
    }

}
