package com.ramogi.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddressesActivity extends AppCompatActivity {

    private Utilities utilities;;
    private EditText enteredPhoneEditText;
    private ArrayList<String> plusUrls = new ArrayList<String>();
    private ArrayList<String> plusShortUrlCode  = new ArrayList<String>();
    private ArrayList<String> plusPropertyName = new ArrayList<String>();
    private ArrayList<String> plusPropertyNumber = new ArrayList<String>();
    private ArrayList<String> plusRoute  = new ArrayList<String>();
    private ArrayList<Drawable> plusImages = new ArrayList<Drawable>();
    private Drawable myimage;
    private String shortUrlCode ;
    private String propertyName;
    private String propertyNumber;
    private String route ;
    private KeenIO keenIO;
    private String phonenumber;
    private int activityposition;
    private ProgressDialog progressBar;
    private Handler progressBarHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addresses_main);

        activityposition = 0;

        //obtain the bundle data
        Bundle bundle = getIntent().getExtras();
        plusUrls = bundle.getStringArrayList("url");
        plusShortUrlCode = bundle.getStringArrayList("shorturl");
        plusPropertyName = bundle.getStringArrayList("propname");
        plusPropertyNumber = bundle.getStringArrayList("propno");
        plusRoute = bundle.getStringArrayList("route");
        phonenumber = bundle.getString("phonenumber");

        setPlusPropertyNumber(plusPropertyNumber);
        setPlusPropertyName(plusPropertyName);
        setPlusRoute(plusRoute);
        setPlusShortUrlCode(plusShortUrlCode);
        setPhonenumber(phonenumber);

        //call the method to download the images and display them
        downloadImages(plusUrls, plusShortUrlCode, plusPropertyName, plusPropertyNumber, plusRoute);

    }
    //method that downloads all the images associated with the number.
    public void downloadImages(final ArrayList<String> plusUrls , final  ArrayList<String> plusShortUrlCode,
                               final ArrayList<String> plusPropertyName, final ArrayList<String> plusPropertyNumber,
                               final ArrayList<String> plusRoute ){

        final LinearLayout lm = (LinearLayout) findViewById(R.id.addresses);

        plusImages.clear();

        activityposition = Constants.DISPLAY_ADDRESS;

        //get the progress bar
        final ProgressDialog progressBar = ProgressBarThings(this);

        //loop through the addresses
        for(int i = 0; i < plusUrls.size(); i++){

            int K = 100/plusUrls.size();
            final int progressBarStatus = (i+1)*K ;
            final int j = i;

            //launch the call back that will download the images
            CallBackImageOne cbi = new CallBackImageOne() {
                @Override
                public void querycomplete(Drawable images) {

                    plusImages.add(images);

                    // Create LinearLayout
                    LinearLayout ll = new LinearLayout(getApplicationContext());
                    ll.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lparams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    // Create TextView
                    TextView propnameTextView = new TextView(getApplicationContext());
                    propnameTextView.setLayoutParams(lparams3);
                    propnameTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    propnameTextView.setText(""+plusPropertyName.get(j));
                    ll.addView(propnameTextView);

                    // Create TextView
                    TextView propnoTextView = new TextView(getApplicationContext());
                    propnoTextView.setLayoutParams(lparams3);
                    propnoTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
                    propnoTextView.setText(plusPropertyNumber.get(j) + ", " + plusRoute.get(j));
                    ll.addView(propnoTextView);

                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int width = dm.widthPixels;
                    int height = width * images.getIntrinsicHeight() / images.getIntrinsicWidth();

                    ImageView imageView = new ImageView(getApplicationContext());
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(width,
                            height);
                    //imageView.setLayoutParams(lparams);
                    lparams.rightMargin = 0;
                    lparams.leftMargin = 0;
                    lparams.width = width;
                    imageView.setLayoutParams(lparams);
                    imageView.setMinimumWidth(width);
                    imageView.setPadding(0,0,0,0);

                    imageView.setImageDrawable(images);
                    ll.addView(imageView);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    ImageButton imageButton = new ImageButton(getApplicationContext());
                    params.gravity = Gravity.RIGHT;
                    params.rightMargin = 24;
                    params.topMargin = -34;
                    params.bottomMargin = 10;
                    //params.width = width;
                    imageButton.setLayoutParams(params);
                    imageButton.setBackground(getResources().getDrawable(R.drawable.shape_circle));
                    imageButton.setBackgroundResource(R.drawable.img_share);
                    imageButton.setOnClickListener(sharebuttonhandler);
                    imageButton.setId(j + 9000);
                    ll.addView(imageButton);

                    //Add button to LinearLayout defined in XML
                    lm.addView(ll);

                    //add the progress bar
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

            //Go download the image
            ImageDownloadOne imageDownloadOne = new ImageDownloadOne(plusUrls.get(i),cbi, this);
            imageDownloadOne.execute();
        }
        setPlusImages(plusImages);
    }

    //method that displays all the images associated with the number.
    public void displayImages(final ArrayList<Drawable> plusImages , final  ArrayList<String> plusShortUrlCode,
                               final ArrayList<String> plusPropertyName, final ArrayList<String> plusPropertyNumber,
                               final ArrayList<String> plusRoute ){

        final LinearLayout lm = (LinearLayout) findViewById(R.id.addresses);
        lm.removeAllViews();

        activityposition = Constants.DISPLAY_ADDRESS;

        //loop through the images and present them on the gui
        for (int j = 0; j < plusImages.size(); j++) {

            // Create LinearLayout
            LinearLayout ll = new LinearLayout(getApplicationContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            // Create TextView
            TextView propnameTextView = new TextView(getApplicationContext());
            propnameTextView.setLayoutParams(lparams2);
            propnameTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
            propnameTextView.setText(plusPropertyName.get(j));
            ll.addView(propnameTextView);

            // Create TextView
            TextView propnoTextView = new TextView(getApplicationContext());
            propnoTextView.setLayoutParams(lparams2);
            propnoTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
            propnoTextView.setText(plusPropertyNumber.get(j) + ", " + plusRoute.get(j));
            ll.addView(propnoTextView);

            //obtain the maximum width for the image
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = width * plusImages.get(j).getIntrinsicHeight() / plusImages.get(j).getIntrinsicWidth();

            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(width,
                    height);

            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setLayoutParams(lparams);
            imageView.setImageDrawable(plusImages.get(j));
            ll.addView(imageView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            ImageButton imageButton = new ImageButton(getApplicationContext());
            params.gravity = Gravity.RIGHT;
            params.rightMargin = 24;
            params.topMargin = -34;
            params.bottomMargin = 10;
            imageButton.setLayoutParams(params);
            imageButton.setBackground(getResources().getDrawable(R.drawable.shape_circle));
            imageButton.setBackgroundResource(R.drawable.img_share);
            imageButton.setOnClickListener(sharebuttonhandler);
            imageButton.setId(j + 9000);
            ll.addView(imageButton);

            //Add button to LinearLayout defined in XML
            lm.addView(ll);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //initialize the progress bar
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
        switch (item.getItemId()) {
            case  R.id.action_feedback:
                sendFeedback();
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Log.v("onoptionsitemselected "," android.r.id.home");
                if(activityposition == Constants.DISPLAY_ADDRESS) {
                    //Intent upIntent = NavUtils.getParentActivityIntent(this);
                    Intent upIntent = new Intent(this, DispatchActivity.class);
                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {

                        Log.v("onoptionsitemselected "," android.r.id.home inside if");
                        // This activity is NOT part of this app's task, so create a new task
                        // when navigating up, with a synthesized back stack.
                        TaskStackBuilder.create(this)
                                // Add all of this activity's parents to the back stack
                                .addNextIntentWithParentStack(upIntent)
                                        // Navigate up to the closest parent
                                .startActivities();
                    } else {
                        Log.v("onoptionsitemselected "," android.r.id.home inside else");
                        // This activity is part of this app's task, so simply
                        // navigate up to the logical parent activity.
                        NavUtils.navigateUpTo(this, upIntent);
                    }
                }
                else if (activityposition == Constants.SHARE_ADDRESS){
                    displayImages(getPlusImages(),getPlusShortUrlCode(),getPlusPropertyName(),getPlusPropertyNumber(),getPlusRoute());

                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    //handle all the button click lister here
    View.OnClickListener sharebuttonhandler = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case 9000:
                    addressshare(0);
                    break;
                case 9001:
                    addressshare(1);
                    break;
                case 9002:
                    addressshare(2);
                    break;
                case 9003:
                    addressshare(3);
                    break;
                case 9004:
                    addressshare(4);
                    break;
                case 9005:
                    addressshare(5);
                    break;
                case 5000:
                    whatsapp();
                    break;
                case 5001:
                    sms();
                    break;
                case 5002:
                    email();
                    break;
                case 5003:
                    Toast.makeText(getApplicationContext(),"google maps", Toast.LENGTH_LONG).show();

                    //Uri gmmIntentUri = Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia");
                    //Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    //mapIntent.setPackage("com.google.android.apps.maps");
                    //startActivity(mapIntent);
/*
                    float lat = 40.714728f;
                    float lng = -73.998672f;

                    String maplLabel = "ABC Label";
                    final Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q="+lat+","+lng+"&z=16 (" + maplLabel + ")"));
                    startActivity(intent);
                    */
                    float latx = -1.22002f;
                    float longy = 36.78232f;

                    String maplabel = "ramogi ochola";
                    Uri mapurl = Uri.parse("google.navigation:q="+latx+","+longy+"&z=10 (" + maplabel + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapurl);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    startService(new Intent(getApplication(), ChatHeadService.class));

                    startActivity(mapIntent);

                    break;
                default:
                    Log.v("onclick", "gosh");
            }
        }
    };

    //launch the whatsapp sharing intent
    public void whatsapp(){
        keenIO = new KeenIO();
        keenIO.sharedto("WHATSAPP", phonenumber, new java.util.Date(), new Long(23092));
        keenIO.sharereason("W"+propertyName,phonenumber,new java.util.Date(), new Long(32893));

        String link = getString(R.string.addresslink1)+" "+ getPropertyName() +" " + getString(R.string.addresslink2) + getShortUrlCode();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
            keenIO.sharesuccess("YES",phonenumber, new java.util.Date(), new Long(23909203));
        } catch (android.content.ActivityNotFoundException ex) {
            keenIO.sharesuccess("NO",phonenumber, new java.util.Date(), new Long(909203));
            Toast.makeText(this,R.string.whatsapperror, Toast.LENGTH_LONG).show();
        }

    }

    //launch the sms sharing of the address
    public void sms(){

        keenIO = new KeenIO();
        keenIO.sharedto("SMS", phonenumber, new java.util.Date(), new Long(23092));
        keenIO.sharereason("S" + propertyName, phonenumber, new java.util.Date(), new Long(32893));

        String link = getString(R.string.addresslink1)+" "+ getPropertyName() +" " + getString(R.string.addresslink2) + getShortUrlCode();

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", link);
        sendIntent.setType("vnd.android-dir/mms-sms");


        Log.v("sms share", " hey sms");

        try {
            startActivity(sendIntent);
            keenIO.sharesuccess("YES",phonenumber, new java.util.Date(), new Long(23909203));
        } catch (android.content.ActivityNotFoundException ex) {
            keenIO.sharesuccess("NO",phonenumber, new java.util.Date(), new Long(909203));
            Toast.makeText(this,R.string.whatsapperror, Toast.LENGTH_LONG).show();
        }

    }

    //launch email sharing of the address
    public void email(){

        keenIO = new KeenIO();
        keenIO.sharedto("SMS", phonenumber, new java.util.Date(), new Long(23092));
        keenIO.sharereason("S"+propertyName,phonenumber,new java.util.Date(), new Long(32893));

        String link = getString(R.string.addresslink1)+" "+ getPropertyName() +" " + getString(R.string.addresslink2) + getShortUrlCode();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.emailrecipient)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailsubject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, link);

        Log.v("email share", " hey email");

        try {
            startActivity(emailIntent);
            keenIO.sharesuccess("YES",phonenumber, new java.util.Date(), new Long(23909203));
        } catch (android.content.ActivityNotFoundException ex) {
            keenIO.sharesuccess("NO",phonenumber, new java.util.Date(), new Long(909203));
            Toast.makeText(this,R.string.whatsapperror, Toast.LENGTH_LONG).show();
        }

    }

    //display the address to be shared on the gui with the whatsapp, sms and email buttons
    public void addressshare(int j){

        activityposition = Constants.SHARE_ADDRESS;

        int k = 0;
        final LinearLayout lm = (LinearLayout) findViewById(R.id.addresses);
        lm.removeAllViews();

        // Create LinearLayout
        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Create TextView
        TextView propnameTextView = new TextView(getApplicationContext());
        propnameTextView.setLayoutParams(lparams);
        propnameTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
        propnameTextView.setText(" " + plusPropertyName.get(j));
        ll.addView(propnameTextView);

        // Create TextView
        TextView propnoTextView = new TextView(getApplicationContext());
        propnoTextView.setLayoutParams(lparams);
        propnoTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
        propnoTextView.setText(plusPropertyNumber.get(j) + ", " + plusRoute.get(j));
        ll.addView(propnoTextView);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = width * plusImages.get(j).getIntrinsicHeight() / plusImages.get(j).getIntrinsicWidth();

        LinearLayout.LayoutParams lparams4 = new LinearLayout.LayoutParams(width,height);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(lparams4);
        imageView.setImageDrawable(plusImages.get(j));
        ll.addView(imageView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageButton whatsapp = new ImageButton(getApplicationContext());
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 24;
        params.topMargin = -60;
        whatsapp.setLayoutParams(params);
        whatsapp.setBackground(getResources().getDrawable(R.drawable.shape_oval));
        whatsapp.setBackgroundResource(R.drawable.img_wa);
        whatsapp.setId(5000 + k + 0);
        whatsapp.setOnClickListener(sharebuttonhandler);
        ll.addView(whatsapp);

        ImageButton sms = new ImageButton(getApplicationContext());
        params.topMargin = 10;
        sms.setLayoutParams(params);
        sms.setBackground(getResources().getDrawable(R.drawable.shape_oval));
        sms.setBackgroundResource(R.drawable.img_sms);
        sms.setId(5000 + k + 1);
        sms.setOnClickListener(sharebuttonhandler);
        ll.addView(sms);

        ImageButton email = new ImageButton(getApplicationContext());
        email.setLayoutParams(params);
        email.setBackground(getResources().getDrawable(R.drawable.shape_oval));
        email.setBackgroundResource(R.drawable.img_email);
        email.setId(5000 + k + 2);
        email.setOnClickListener(sharebuttonhandler);
        ll.addView(email);

        ImageButton maps = new ImageButton(getApplicationContext());
        maps.setLayoutParams(params);
        maps.setBackground(getResources().getDrawable(R.drawable.shape_oval));
        maps.setBackgroundResource(R.drawable.img_email);
        maps.setId(5000 + k + 3);
        maps.setOnClickListener(sharebuttonhandler);
        ll.addView(maps);

        //Add button to LinearLayout defined in XML
        lm.addView(ll);

        setMyimage(plusImages.get(j));
        setShortUrlCode(plusShortUrlCode.get(j));
        setPropertyName(plusPropertyName.get(j));
        setPropertyNumber(plusPropertyNumber.get(j));
        setRoute(plusRoute.get(j));

    }

    @Override
    public void onBackPressed() {

        switch (activityposition){
            case 0:
                Log.v("navigation", " back pressed ");
                Intent intent = new Intent(this, DispatchActivity.class);
                startActivity(intent);
                break;
            case 1:
                Intent intent2 = new Intent(this, DispatchActivity.class);
                startActivity(intent2);
                break;
            case 2:
                displayImages(getPlusImages(),getPlusShortUrlCode(),getPlusPropertyName(),getPlusPropertyNumber(),getPlusRoute());
                break;
            default:
                Log.v("navigation", " do nothing ");
        }
    }

    public Drawable getMyimage() {
        return myimage;
    }

    public void setMyimage(Drawable myimage) {
        this.myimage = myimage;
    }

    public String getShortUrlCode() {
        return shortUrlCode;
    }

    public void setShortUrlCode(String shortUrlCode) {
        this.shortUrlCode = shortUrlCode;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyNumber() {
        return propertyNumber;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public ArrayList<Drawable> getPlusImages() {
        return plusImages;
    }

    public void setPlusImages(ArrayList<Drawable> plusImages) {
        this.plusImages = plusImages;
    }


    public ArrayList<String> getPlusUrls() {
        return plusUrls;
    }

    public void setPlusUrls(ArrayList<String> plusUrls) {
        this.plusUrls = plusUrls;
    }

    public ArrayList<String> getPlusShortUrlCode() {
        return plusShortUrlCode;
    }

    public void setPlusShortUrlCode(ArrayList<String> plusShortUrlCode) {
        this.plusShortUrlCode = plusShortUrlCode;
    }

    public ArrayList<String> getPlusPropertyName() {
        return plusPropertyName;
    }

    public void setPlusPropertyName(ArrayList<String> plusPropertyName) {
        this.plusPropertyName = plusPropertyName;
    }

    public ArrayList<String> getPlusPropertyNumber() {
        return plusPropertyNumber;
    }

    public void setPlusPropertyNumber(ArrayList<String> plusPropertyNumber) {
        this.plusPropertyNumber = plusPropertyNumber;
    }

    public ArrayList<String> getPlusRoute() {
        return plusRoute;
    }

    public void setPlusRoute(ArrayList<String> plusRoute) {
        this.plusRoute = plusRoute;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}