package com.ramogi.myapplication;

/**
 * Created by ROchola on 2/24/2016.
 *
 * This is a class for common methods used throughout the application
 */

public class Utilities {

    //Check if its a valid mobile phone number
    public boolean checkPhone(String phone){
        boolean result = false;

        if((phone.startsWith("07"))&&(phone.length()==10)){
            result = true;
        }

        return result;
    }
}
