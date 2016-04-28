package com.ramogi.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class GeoPoints  extends AppCompatActivity {

    private Button btnthemall,btnyaya,btnlavi;
    private int activityposition;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private double latx,laty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_geo_points);

        activityposition = 0;

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.app_name);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ab.show();


        btnlavi = (Button)findViewById(R.id.button3);
        btnthemall = (Button) findViewById(R.id.button);
        btnyaya = (Button) findViewById(R.id.button2);

        btnyaya.setOnClickListener(buttonhandler);
        btnthemall.setOnClickListener(buttonhandler);
        btnlavi.setOnClickListener(buttonhandler);



    }

    //this method calls parse backend and retrieves points nearest to the point
    public void arrangebynearestlocations(double latx, double laty){

        //Create the parsegeopoint for the user's location
        ParseGeoPoint userLocation= new ParseGeoPoint(latx, laty);

        //run the query
        ParseQuery<OkhiDataModel> query = OkhiDataModel.getQuery();
        query.whereNear("geoPoint", userLocation);
        query.setLimit(10); //set the limit to 10

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

    //all the button clicks are handled here
    View.OnClickListener buttonhandler = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.button:
                    latx = -1.2643764;
                    laty = 36.8027191;
                    Log.v("geopoint ", "the mall "+latx+ " "+laty);
                    arrangebynearestlocations(latx,laty);
                    break;
                case R.id.button2:
                    latx = -1.2929798;
                    laty = 36.7878277;
                    Log.v("geopoint ", "yaya center "+latx+ " "+laty);
                    arrangebynearestlocations(latx, laty);
                    break;
                case R.id.button3:
                    latx = -1.27988;
                    laty = 36.770266;
                    Log.v("geopoint ", "lavington green "+latx+ " "+laty);
                    arrangebynearestlocations(latx, laty);
                    break;
                default:



            }

        }
    };

    //the method that works on the gui to display the addresses
    public void displaynearestaddress(List<OkhiDataModel> objects){

        int k = 0;
        activityposition = Constants.NEAREST_LOCATIONS;
        setContentView(R.layout.nearestaddresses);
        final CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.nearest);

        //A reference to the layout that will carry the views generated below
        RelativeLayout rl = (RelativeLayout) cl.getChildAt(0);

        //a reference to the nested layout that carries the views
        final LinearLayout lm = (LinearLayout) rl.findViewById(R.id.addresses);

        //remove any views so since we are recreating views afresh
        lm.removeAllViews();

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
        //lm.addView(llone);




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

        final ProgressDialog progressBar = ProgressBarThings(this);

        for(int i=0; i < u;i++){

            //int K = 100/plusUrls.size();
            final int progressBarStatus = (i+1)*20 ;
            final ParseObject odm = objects.get(i);

            //launch the callback
            CallBackImageOne cbi = new CallBackImageOne() {
                @Override
                public void querycomplete(Drawable images) {

                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int width = dm.widthPixels;
                    int height = width * images.getIntrinsicHeight() / images.getIntrinsicWidth();

                    LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(width,height);

                    // Create TextView
                    TextView propnameTextView = new TextView(getApplicationContext());
                    propnameTextView.setLayoutParams(paramsButton);
                    propnameTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    propnameTextView.setText(odm.getString("propertyName"));
                    lltwo.addView(propnameTextView);

                    // Create TextView
                    TextView propnoTextView = new TextView(getApplicationContext());
                    propnoTextView.setLayoutParams(paramsButton);
                    propnoTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    propnoTextView.setText(odm.getString("propertyNumber") + ", " + odm.getString("route"));
                    lltwo.addView(propnoTextView);

                    ImageView imageView = new ImageView(getApplicationContext());

                    imageView.setLayoutParams(paramsImage);

                    imageView.setImageDrawable(images);
                    lltwo.addView(imageView);

                    progressBarHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setProgress(progressBarStatus);

                        }
                    });

                    Log.v("downloadimages ", "value of k inside cbi " + progressBarStatus);

                    if (progressBarStatus >= 100) {

                        // sleep 2 seconds, so that you can see the 100%
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // close the progress bar dialog
                        progressBar.dismiss();
                    }

                }
            };

            //Download the images
            ImageDownloadOne imageDownloadOne = new ImageDownloadOne(
                    odm.getParseFile("gatePhotoMedium").getUrl(),cbi,this);
            imageDownloadOne.execute();
        }
        lm.addView(lltwo);

    }

    //initialize the progress bar that will be shown when things are downloading
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


        Log.v(" navigation ", " "+activityposition);

        switch (item.getItemId()) {
            case  R.id.action_feedback:
                sendFeedback();

                return true;
            // Respond to the action bar's Up/Home button
            case R.id.home:
                if(activityposition == Constants.NEAREST_LOCATIONS){
                    Log.v(" navigation ", " setting content");
                    setContentView(R.layout.activity_geo_points);

                }
                else {
                    Log.v(" navigation ", " going to welcome screent");
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    startActivity(intent);

                }


                return true;
            default:
                Log.v("navigation"," no match");
                if(activityposition == Constants.NEAREST_LOCATIONS){
                    Log.v(" navigation ", " setting content");
                    Intent intent = new Intent(this, GeoPoints.class);
                    startActivity(intent);

                }
                else {
                    Log.v(" navigation ", " going to welcome screent");
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    startActivity(intent);

                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        switch (activityposition) {
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
                if (activityposition == Constants.NEAREST_LOCATIONS) {
                    Log.v(" navigation ", " setting content");
                    Intent geoIntent = new Intent(this, GeoPoints.class);
                    startActivity(geoIntent);

                } else {
                    Log.v(" navigation ", " going to welcome screent");
                    Intent welintent = new Intent(this, WelcomeActivity.class);
                    startActivity(welintent);

                }
                break;
            default:
                Intent intent3 = new Intent(this, WelcomeActivity.class);
                startActivity(intent3);
                Log.v("navigation", " do nothing ");
        }



        }

}
