package uk.asimrehman.crimemap.Framework.Adapters;

import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.asimrehman.crimemap.R;

/**
 * Created by admin on 07/10/2016.
 */

/**
 *  This class is for addressList, which is shown
 *  when the user searches for a particular location / addressList.
 */
public class AddressAdapter extends ArrayAdapter<Address> {

    private Context context;
    private List<Address> addressList;
    private LayoutInflater inflater;

    public AddressAdapter(Context context, List<Address> objects) {
        super(context, 0, objects);

        this.context = context;
        this.addressList = objects;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.select_dialog_singlechoice, parent, false);
        }

        TextView tvAddress = (TextView) convertView.findViewById(android.R.id.text1);

        if (Build.VERSION.SDK_INT < 23) {
            tvAddress.setTextAppearance(getContext(), R.style.AddressRowStyle);
        } else {
            tvAddress.setTextAppearance(R.style.AddressRowStyle);
        }

        Address addressObject = addressList.get(position);
        Set<String> mySet = new LinkedHashSet<>();


        for(int i=0;i<addressObject.getMaxAddressLineIndex();i++)
        {
            mySet.add(addressObject.getAddressLine(i));
        }

        if (addressObject.getPostalCode() != null)
            mySet.add(addressObject.getPostalCode());

        if (addressObject.getCountryCode() != null)
            mySet.add(addressObject.getCountryCode());

        if (addressObject.getCountryName() != null)
            mySet.add(addressObject.getCountryName());

        if (addressObject.getLocality() != null)
           mySet.add(addressObject.getLocality());

        if (addressObject.getAdminArea() != null)
            mySet.add(addressObject.getAdminArea());

        if(addressObject.getSubAdminArea() != null)
            mySet.add(addressObject.getSubAdminArea());

        String address = Arrays.toString(mySet.toArray());
        address = address.replaceAll("([^A-Za-z0-9 ,])","");
        tvAddress.setText(address);

        return convertView;
    }


    @Nullable
    @Override
    public Address getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return addressList.size();
    }



}
