package com.ramogi.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import com.parse.ParseUser;


/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (WelcomeActivity) activity.
 */

public class DispatchActivity extends Activity {

    private int progressBarStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);


        Log.v("dispatch", " activity called");

        progressBarStatus = 0;
        //tv = (TextView) findViewById(R.id.textView6);


        new Thread(new Runnable() {
            public void run() {
                for(int i = 3; i >= 0; i--){
                    final int k = i;
                    Log.v("runnable ", " i = " + i);

                    // When you need to modify a UI element, do so on the UI thread.
                    // 'getActivity()' is required as this is being ran from a Fragment.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(k  == 0){
                                checkuser();
                            }
                            else {

                            }
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.

                        }
                    });

                    // your computer is too fast, sleep 2 seconds
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    //method for checking new user
    private void checkuser(){
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {

            Log.v("current user", "old guy");

            // Start an intent for the logged in activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Log.v("current user", "new guy");

            // Start and intent for the logged out activity
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
