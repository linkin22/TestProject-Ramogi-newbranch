package com.ramogi.myapplication;

import android.os.Build;
import com.parse.ParseUser;
import java.util.Random;

/**
 * Created by ROchola on 2/23/2016.
 *
 * This is a class that saves all the constants used throughout this application
 */


public class Constants {

    //parse keys
    public static final String APPLICATION_ID = "JMSOFZr2pwk0avRymTYPswuIpfy8qP7p5kZhvIpL";
    public static final String CLIENT_KEY = "qEer2q8xGr85y412JBLABxkOvzOFnuQg1U25bsYc";

    //rollbar keys
    public static final String ROLLBAR_SERVER = "8175bd7d375547a4945df4a22a92e8e7";
    public static final String ROLLBAR_CLIENT = "1d34d3ba07084477841128ae29810e2c";

    //keen keys
    public static final String KEEN_WRITE_KEY = "6b3cccceb9abb842d25226423f61a6786d557392357a05b9d2557b40fd1dd25463fb1ee09abdb76c6e6dca941051bcb8474cebf2eaddcfe12aea900b47a4dfdbf288a8e4af5f914d507d902ad3d9320615f419ac26c5305848ceecb2a658898f";
    public static final String KEEN_READ_KEY = "cc22284828e54d7102afbb0c9af4d20e409ac2eb62240409467e2989a8b793910378489b45113a8007bba29bff3d441ca57e5dcf9a03a13f29d045aef5e07f2292a5567cc535ed4a028205855e6624dfe88e0ae92c4d9aa811c390ec6b890e94";
    public static final String KEEN_MASTER_KEY = "18A752B9E025E2447D620B39A71A9E4262E504B38A9A261016CBB4C3AD20DDDB";
    public static final String KEEN_PROJECT_ID = "56cc3bc559949a331b1ffb12";

    //event names for keen
    public static final String EVENT_ADDRESSFOUND = "addressFound";
    public static final String EVENT_ADDRESSNOTFOUND = "addressNotFound";
    public static final String EVENT_SHARED = "shared";
    public static final String EVENT_SHARESUCCESS = "shareSuccess";
    public static final String EVENT_SHAREREASON = "shareReason";

    //values that will be used to influence the behavior of navigation in the app
    public static final int DISPLAY_ADDRESS = 1;
    public static final int SHARE_ADDRESS =  2;
    public static final int GEO_POINTS = 3;
    public static final int NEAREST_LOCATIONS = 4;


    //The below are common values used for keen logging events
    public static String getuserid(){
        return  "userid";
        //return ""+ParseUser.getCurrentUser().getObjectId().toString();
    }

    public static int getAppVersion(){
        return BuildConfig.VERSION_CODE;
    }

    public static String getdeviceos(){
        return Build.VERSION.RELEASE;
    }

    public static int getsessionid() {

        int max = 999999999;
        int min = 100000000;

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
