package com.ramogi.myapplication;

import android.app.Application;

import com.parse.Parse;
import com.rollbar.android.Rollbar;

import io.keen.client.android.AndroidKeenClientBuilder;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenLogging;
import io.keen.client.java.KeenProject;

/**
 * Created by ROchola on 2/24/2016.
 *
 * This class initializes parse, keen and rollbar so they don't get called again throughout the application
 *
 */

public class Common extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, Constants.APPLICATION_ID, Constants.CLIENT_KEY);
        Rollbar.init(this, Constants.ROLLBAR_CLIENT, "production");

        // If the Keen Client isn't already initialized, initialize it.
        if (!KeenClient.isInitialized()) {
            // Create a new instance of the client.
            KeenClient client = new AndroidKeenClientBuilder(this).build();

            // Get the project ID and write key from string resources, then create a project and set
            // it as the default for the client.
            //String projectId = getString(R.string.keen_project_id);
            //String writeKey = getString(R.string.keen_write_key);
            KeenProject project = new KeenProject(Constants.KEEN_PROJECT_ID, Constants.KEEN_WRITE_KEY, Constants.KEEN_READ_KEY);
            client.setDefaultProject(project);

            // During testing, enable logging and debug mode.
            // NOTE: REMOVE THESE LINES BEFORE SHIPPING YOUR APPLICATION!
            KeenLogging.enableLogging();
            client.setDebugMode(true);

            // Initialize the KeenClient singleton with the created client.
            KeenClient.initialize(client);


        }
    }
}
