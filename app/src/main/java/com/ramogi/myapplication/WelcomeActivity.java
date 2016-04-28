package com.ramogi.myapplication;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.rollbar.android.Rollbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.keen.client.java.KeenClient;

/*
This activity is called by the dispatch activity when it's a new parse user.
It is very similar to mainactivity except it has a gps button

 */
public class WelcomeActivity extends AppCompatActivity  {

    private Utilities utilities;;
    private KeenIO keenIO;
    private EditText enteredPhoneEditText;
    private ArrayList<String> plusUrls = new ArrayList<String>();
    private ArrayList<String> plusShortUrlCode  = new ArrayList<String>();
    private ArrayList<String> plusPropertyName = new ArrayList<String>();
    private ArrayList<String> plusPropertyNumber = new ArrayList<String>();
    private ArrayList<String> plusRoute  = new ArrayList<String>();
    private ArrayList<Drawable> plusImages = new ArrayList<Drawable>();
    private ArrayList<ParseGeoPoint> plusGeoPoints = new ArrayList<ParseGeoPoint>();
    private String sharephone;
    private Button gpsbutton;
    private double latx;
    private double laty;
    private int activityposition;
    private ProgressDialog progress;


    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        activityposition = 0;

        //send a test report message to rollbar
        Rollbar.reportMessage("A test message from ramogi", "debug");

        //initialize keen event logging
        keenIO = new KeenIO();

        //Initialize the toolbar variable
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(buttonhandler);

        //initialize the gps button and set a click listener on it
        gpsbutton = (Button) findViewById(R.id.button2);
        gpsbutton.setOnClickListener(buttonhandler);

        //initialize the edittest that will get the user's input.
        enteredPhoneEditText = (EditText)  findViewById(R.id.editText2);

        //autoshow numeric keyboard
        showSoftKeyboard(enteredPhoneEditText);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //initialize the imagebutton that will respond to the user's input of finding addresses associated with a certain number
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //call the method that will process the user's input
                testingParse();
            }
        });
    }

    //the method that autoshows the numeric keyboard
    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    //the method that hides the keyboard
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
                            //log the event
                            keenIO.addressfound(phonenumber,new java.util.Date(),new Long(30930923));

                            //reset the url arraylist because we are about to populate it with fresh data.
                            plusUrls.clear();

                            //iterate through the data
                            for (ParseObject pobject : arraythis) {

                                //work on those with short url code
                                if(!(pobject.getString("shortUrlCode").isEmpty())){

                                    //checking through all the required variable
                                    try{

                                        //get the required variables from the parse object
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

                            TextView noPhoneNoTextView = (TextView) findViewById(R.id.textView6);
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
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    //Start a new activity for sending a feedback email
    private void sendFeedback() {
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

        hideSoftKeyboard(enteredPhoneEditText);

        Log.v(" navigation ", item.toString());

        switch (item.getItemId()) {
            case  R.id.action_feedback:
                sendFeedback();

                return true;
            // Respond to the action bar's Up/Home button
            case R.id.home:
                if(activityposition == Constants.GEO_POINTS) {
                    Intent intent = new Intent(this, DispatchActivity.class);
                    startActivity(intent);
                }
                else if (activityposition == Constants.NEAREST_LOCATIONS){
                    gpsbuttoncalled();

                }

                return true;
            default:
                Log.v("navigation"," no match");
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

    //call the activity that will display the found addresses
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

    protected void track() {
        // Create an event to upload to Keen.
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("item", "golden widget");

        // Add it to the "purchases" collection in your Keen Project.
        //KeenClient.client().addEvent("purchases", event);
        KeenClient.client().queueEvent("okhiproject", event);

    }

    //check for network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    protected void onPause() {

        //check for network availabilty
        if(isNetworkAvailable()){
            // Send all queued events to Keen. Use the asynchronous method to
            // avoid network activity on the main thread.
            KeenClient.client().sendQueuedEventsAsync();
        }
        super.onPause();
    }

    //handle all clicklistener callbacks here
    View.OnClickListener buttonhandler = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button2:
                    gpsbuttoncalled();
                    break;
                case R.id.toolbar:
                    Log.v("toolbar"," I've been pressed ");
                    break;
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    if(activityposition == Constants.GEO_POINTS) {
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                    }
                    else if (activityposition == Constants.NEAREST_LOCATIONS){
                        gpsbuttoncalled();

                    }
                    break;
                default:
                    Log.v("onclick", "gosh "+v.toString() +" "+v.getId());

                    if(activityposition == Constants.GEO_POINTS) {
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                    }
                    else if (activityposition == Constants.NEAREST_LOCATIONS){
                        gpsbuttoncalled();

                    }
            }

        }
    };

    //This method is called when the user click the gps button on the gui
    public void gpsbuttoncalled(){

        //launch the geopoint activity
        Intent geoPointsIntent = new Intent(this, GeoPoints.class);
        startActivity(geoPointsIntent);
    }

  /*

    //this method calls parse backend and retrieves points nearest to the point
    public void arrajngebynearestlocations(double latx, double laty){

        //Create the parsegeopoint for the user's location
        ParseGeoPoint userLocation = new ParseGeoPoint(latx, laty);

        //run the query
        ParseQuery<OkhiDataModel> query = OkhiDataModel.getQuery();
        query.whereNear("geoPoint", userLocation);
        query.setLimit(10); //sets the limit

        //run it asynchronously
        query.findInBackground(new FindCallback<OkhiDataModel>() {
            @Override
            public void done(List<OkhiDataModel> objects, ParseException e) {

                if (e == null) {
                    //call the method that will display the addresses
                    displaynearestaddress(objects);
                } else {
                    Log.v("parse query", " this query is messed up " + e.toString());
                }

            }
        });

    }


/*
    //the method that works on the gui to display the addresses
    public void displaynearestaddress(List<OkhiDataModel> objects){

        int k = 0;
        activityposition = Constants.NEAREST_LOCATIONS;
        setContentView(R.layout.nearestaddresses);

        //get a handle on the layout
        final CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.nearest);

        //get a handle on a particular layout. this is the layout that will display the addresses
        RelativeLayout rl = (RelativeLayout) cl.getChildAt(1);

        //get a reference to the toolbar, setup a click listener, ensure homebutton is displayed e.t.c.
        Toolbar toolbar = (Toolbar) cl.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(buttonhandler);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.app_name);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ab.show();

        //get a reference to a particular layout
        final LinearLayout lm = (LinearLayout) rl.findViewById(R.id.addresses);

        //remove all views because we want to start afresh with new views
        lm.removeAllViews();

        let's start constructing the new views and add them to the layout

       /*
        final LinearLayout llone = new LinearLayout(getApplicationContext());
        llone.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams paramslinear = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramslinear.gravity = Gravity.RIGHT;
        paramslinear.topMargin = 40;
        paramslinear.rightMargin = 20;

        Button btnthemall = new Button(getApplicationContext());
        btnthemall.setLayoutParams(paramslinear);
        btnthemall.setText("The Mall");
        btnthemall.setId(5000 + k + 0);
        btnthemall.setOnClickListener(buttonhandler);
        btnthemall.setBackgroundColor((Color.rgb(70, 80, 90)));
        llone.addView(btnthemall);

        k = 1;

        Button btnyaya = new Button(getApplicationContext());
        btnyaya.setLayoutParams(paramslinear);
        btnyaya.setText("Yaya Center");
        btnyaya.setId(5000 + k + 0);
        btnyaya.setOnClickListener(buttonhandler);
        btnyaya.setBackgroundColor((Color.rgb(70, 80, 90)));
        llone.addView(btnyaya);

        k = 2;

        Button btnlg = new Button(getApplicationContext());
        btnlg.setLayoutParams(paramslinear);
        btnlg.setText("Lavington Green");
        btnlg.setId(5000 + k + 0);
        btnlg.setOnClickListener(buttonhandler);
        btnlg.setBackgroundColor((Color.rgb(70, 80, 90)));
        llone.addView(btnlg);
        lm.addView(llone);

        // Create LinearLayout
        final LinearLayout lltwo = new LinearLayout(getApplicationContext());
        lltwo.setOrientation(LinearLayout.VERTICAL);

        int u = 0;

        if(objects.size() >5){
            u = 5;
        }
        else{
            u = objects.size();
        }

        //initialize the progressbar
        final ProgressDialog progressBar = ProgressBarThings(this);

        for(int i=0; i < u;i++){

            final int progressBarStatus = (i+1)*20 ;
            final ParseObject odm = objects.get(i);

            //initialize the call back image
            CallBackImageOne cbi = new CallBackImageOne() {
                @Override
                public void querycomplete(Drawable images) {

                    // Create TextView
                    TextView propnameTextView = new TextView(getApplicationContext());

                    propnameTextView.setText(odm.getString("propertyName"));
                    lltwo.addView(propnameTextView);

                    // Create TextView
                    TextView propnoTextView = new TextView(getApplicationContext());
                    propnoTextView.setText(odm.getString("propertyNumber") + ", " + odm.getString("route"));
                    lltwo.addView(propnoTextView);

                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageDrawable(images);
                    lltwo.addView(imageView);

                    progressBarHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBarStatus);

                        }
                    });

                    if (progressBarStatus >= 100) {

                        // sleep 2 seconds, so that you can see the 100%
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // close the progress bar dialog
                        progressBar.dismiss();
                    }

                }
            };

            //download images
            ImageDownloadOne imageDownloadOne = new ImageDownloadOne(
                    odm.getParseFile("gatePhotoMedium").getUrl(),cbi,this);
            imageDownloadOne.execute();
        }
        lm.addView(lltwo);
    }

    */

    //Create a progress bar to be used during downloads
    public ProgressDialog ProgressBarThings(Context context)
    {
        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(true);
        progressBar.setMessage(getString(R.string.downloadprogress));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        return  progressBar;
    }

    @Override
    public void onBackPressed() {

        switch (activityposition){
            case 0:
                Log.v("navigation", " back pressed ");
                Intent intent = new Intent(this, DispatchActivity.class);
                startActivity(intent);
                break;
            case 3:
                Intent intent2 = new Intent(this, DispatchActivity.class);
                startActivity(intent2);
                break;
            case 4:
                gpsbuttoncalled();
                break;
            default:
                Log.v("navigation", " do nothing ");
        }
    }

}