package com.ramogi.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.rollbar.android.Rollbar;


import java.util.ArrayList;
import java.util.HashMap;
import io.keen.client.java.KeenClient;

/*
This activity is called when the user is a repeat user, from the dispatch activity

 */


public class MainActivity extends AppCompatActivity  {

    private Utilities utilities;;
    private KeenIO keenIO;
    private EditText enteredPhoneEditText;
    private ImageButton imageButton;
    private ArrayList<String> plusUrls = new ArrayList<String>();
    private ArrayList<String> plusShortUrlCode  = new ArrayList<String>();
    private ArrayList<String> plusPropertyName = new ArrayList<String>();
    private ArrayList<String> plusPropertyNumber = new ArrayList<String>();
    private ArrayList<String> plusRoute  = new ArrayList<String>();
    private ArrayList<Drawable> plusImages = new ArrayList<Drawable>();
    private String sharephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //upload a test rollbar message
        Rollbar.reportMessage("A test message from ramogi", "debug");

        //Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializing the variable that will get the user's phone no.
        enteredPhoneEditText = (EditText)  findViewById(R.id.editText2);

        //autoshow numeric keyboard
        showSoftKeyboard(enteredPhoneEditText);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //initialize the imagebutton and set up a click listener event
        imageButton = (ImageButton) findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //process the user's input
                testingParse();
            }
        });
    }

    //method for autoshowing the numeric keyboard
    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    //the method for hiding the keyboard
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*despite the method name, this function queries the addresses associated with the phone number. at first it
    for testing parse database connection and then it grew from there.
     */
    public void testingParse() {

        utilities = new Utilities();

        //the user's raw phone number input
        final String enteredPhoneNo = enteredPhoneEditText.getText().toString();

        //varialble phone number in the database format
        final String phonenumber;

        //check if user's input is correct
        if(utilities.checkPhone(enteredPhoneNo)){

            //format user's phone number to the database format
            phonenumber = "+2547"+enteredPhoneNo.substring(2);

            Log.v("testing parse ", phonenumber);

            //parameter used to query the db
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("phone", phonenumber);

            sharephone = phonenumber;

            //query the database asynchronously
            ParseCloud.callFunctionInBackground("getAddressByPhone", params, new FunctionCallback<ArrayList<ParseObject>>() {
                public void done(ArrayList<ParseObject> arraythis, ParseException e) {

                    //check the response is ok
                    if (e == null) {

                        //check we have some data
                        if (arraythis.size() > 0) {

                            keenIO = new KeenIO();
                            keenIO.addressfound(phonenumber,new java.util.Date(),new Long(30930923));

                            plusUrls.clear();

                            //iterate through the data
                            for (ParseObject pobject : arraythis) {

                                //work on those with short url code
                                if(!(pobject.getString("shortUrlCode").isEmpty())){

                                    //get the required variables from the parse object
                                    try{

                                        String imageurl = pobject.getParseObject("AFL").getParseFile("gatePhotoMedium").getUrl();
                                        String shortUrlCode = pobject.getString("shortUrlCode");
                                        String propertyName = pobject.getParseObject("AFL").getString("propertyName");
                                        String propertyNumber = pobject.getParseObject("AFL").getString("propertyNumber");
                                        String route = pobject.getParseObject("AFL").getString("route");

                                        //transferring the obtained variables into an arraylist
                                        plusUrls.add(imageurl);
                                        plusShortUrlCode.add(shortUrlCode);
                                        plusPropertyNumber.add(propertyNumber);
                                        plusRoute.add(route);
                                        plusPropertyName.add(propertyName);

                                        //call the method that will display the addresses
                                        displaytheaddresses();


                                    }
                                    catch (NullPointerException npe){
                                        Log.v("test parse ", " json didn't work " +npe.toString());

                                    }
                                }
                                else{
                                    Log.v("test parse ", " short url not found ");

                                }

                            }
                        }


                    } else {

                        if(isNetworkAvailable()) {

                            keenIO.addressnotfound(phonenumber, new java.util.Date(), new Long(232389822));

                            TextView noPhoneNoTextView = (TextView)findViewById(R.id.textView6);
                            noPhoneNoTextView.setText(R.string.noaddress);
                            enteredPhoneEditText.setText("");
                            enteredPhoneEditText.setHint(enteredPhoneNo);

                            Log.v("so not ok ", e.toString());


                        }
                        else{
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        }
        else{
            Toast.makeText(this,R.string.invalidphone, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    //Start a new activity for sending a feedback email
    private void sendFeedback() {
        hideSoftKeyboard(enteredPhoneEditText);
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/html");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getString(R.string.mail_feedback_email) });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.mail_feedback_subject));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.mail_feedback_message));
        startActivity(emailIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_feedback) {
            sendFeedback();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///give focus to edit text in onResume method.
    @Override
    public void onResume() {
        super.onResume();

        if(isNetworkAvailable()) {

            enteredPhoneEditText.setFocusableInTouchMode(true);
            enteredPhoneEditText.requestFocus();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.networkerror),
                    Toast.LENGTH_LONG).show();
        }
    }

    //call the class that will display the addresses
    public void displaytheaddresses(){

        hideSoftKeyboard(enteredPhoneEditText);

        Intent intent = new Intent(this, AddressesActivity.class);
        intent.putStringArrayListExtra("url", plusUrls);
        intent.putStringArrayListExtra("shorturl", plusShortUrlCode);
        intent.putStringArrayListExtra("propno", plusPropertyNumber);
        intent.putStringArrayListExtra("route", plusRoute);
        intent.putStringArrayListExtra("route", plusRoute);
        intent.putStringArrayListExtra("propname", plusPropertyName);
        intent.putExtra("phonenumber", sharephone);
        startActivity(intent);

    }

    //checking for network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(isNetworkAvailable()) {
            // Send all queued events to Keen. Use the asynchronous method to
            // avoid network activity on the main thread.
            KeenClient.client().sendQueuedEventsAsync();


        }
    }
}