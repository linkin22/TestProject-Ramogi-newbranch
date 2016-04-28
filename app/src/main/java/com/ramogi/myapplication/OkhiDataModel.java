package com.ramogi.myapplication;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by ROchola on 2/27/2016.
 * Objectified class for the datamodel we wish to extract from the parse backend
 */


/**
 * Data model for a post.
 */
@ParseClassName("AddressFoundationLayer")
public class OkhiDataModel extends ParseObject {

    public String getPropertyName(){
        return getString("propertyName");
    }

    public void setPropertyName(String value){
        put("propertyName", value);
    }

    public String getPropertyNumber(){
        return getString("propertyNumber");
    }

    public void setPropertyNumber(String value){
        put("propertyNumber",value);
    }

    public String getRoute(){
        return getString("route");
    }

    public void setRoute(String value){
        put("route", value);
    }

    public String getImageUrl(){
        return getParseFile("gatePhotoMedium").getUrl();
    }

    public void setImageUrl(ParseFile value){
        put("gatePhotoMedium", value);
    }


    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("geoPoint");
    }

    public void setLocation(ParseGeoPoint value) {
        put("geoPoint", value);
    }

    public static ParseQuery<OkhiDataModel> getQuery() {
        return ParseQuery.getQuery(OkhiDataModel.class);
    }
}
