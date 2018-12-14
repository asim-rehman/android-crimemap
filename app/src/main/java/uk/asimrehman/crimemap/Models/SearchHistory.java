package uk.asimrehman.crimemap.Models;



import android.location.Address;

import com.orm.SugarRecord;
import java.util.List;

import uk.asimrehman.crimemap.Framework.HelperMethods;


/**
 * Created by admin on 19/10/2016.
 */


public class SearchHistory extends SugarRecord {

    String addressLine;
    String adminArea;
    String countryCode;
    String countryName;
    String featureName;
    String postCode;
    String subAdminArea;
    String subLocality;
    String locality;
    double latitude;
    double longitude;


    public SearchHistory()
    {}

    public SearchHistory(String addressLine, String adminArea, String countryCode, String countryName, String featureName,
                         String postCode, String subAdminArea, String subLocality, String locality, double latitude, double longitude) {
        this.addressLine = addressLine;
        this.adminArea = adminArea;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.featureName = featureName;
        this.postCode = postCode;
        this.subAdminArea = subAdminArea;
        this.subLocality = subLocality;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SearchHistory(Address address)
    {
        String addressLine = HelperMethods.getAddressLine(address);
        this.addressLine = addressLine;
        this.adminArea = address.getAdminArea();
        this.countryCode = address.getCountryCode();
        this.countryName = address.getCountryName();
        this.featureName = address.getFeatureName();
        this.postCode = address.getPostalCode();
        this.subAdminArea = address.getSubAdminArea();
        this.subLocality = address.getSubLocality();
        this.locality = address.getLocality();
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public void setSubAdminArea(String subAdminArea) {
        this.subAdminArea = subAdminArea;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString()
    {
        return getAddressLine();
    }

    public List<Crimes> GetCrimes()
    {
        return Crimes.find(Crimes.class, " Search_History = ?", new String[]{String.valueOf(getId())});
    }
}
