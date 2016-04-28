package com.ramogi.myapplication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.keen.client.java.KeenClient;

/**
 * Created by ROchola on 2/25/2016.
 *
 * The class used for logging keen events
 */
public class KeenIO {

    public void addressfound(String phoneNumber, Date timestamp, Long sessionDuration){

        // Create an event to upload to Keen.
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("userId", Constants.getuserid());
        event.put("phoneNumber", phoneNumber);
        event.put("app_Version", Constants.getAppVersion());
        event.put("deviceOS", Constants.getdeviceos());
        event.put("timestamp", timestamp);
        event.put("sessionDuration", sessionDuration);
        event.put("sessionID", Constants.getsessionid());

        // Add it to the "purchases" collection in your Keen Project.
        //KeenClient.client().addEvent("purchases", event);
        KeenClient.client().queueEvent(Constants.EVENT_ADDRESSFOUND, event);

    }
    public void addressnotfound(String phoneNumber, Date timestamp, Long sessionDuration){

        // Create an event to upload to Keen.
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("userId", Constants.getuserid());
        event.put("phoneNumber", phoneNumber);
        event.put("app_Version", Constants.getAppVersion());
        event.put("deviceOS", Constants.getdeviceos());
        event.put("timestamp", timestamp);
        event.put("sessionDuration", sessionDuration);
        event.put("sessionID", Constants.getsessionid());

        KeenClient.client().queueEvent(Constants.EVENT_ADDRESSNOTFOUND, event);

    }
    public void sharedto(String mediaplatform,String phoneNumber, Date timestamp, Long sessionDuration){

        // Create an event to upload to Keen.
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("userId", Constants.getuserid());
        event.put("phoneNumber", phoneNumber);
        event.put("app_Version", Constants.getAppVersion());
        event.put("deviceOS", Constants.getdeviceos());
        event.put("timestamp", timestamp);
        event.put("sessionDuration", sessionDuration);
        event.put("sessionID", Constants.getsessionid());
        event.put("media", mediaplatform);

        KeenClient.client().queueEvent(Constants.EVENT_SHARED, event);
    }
    public void sharesuccess(String yesno,String phoneNumber, Date timestamp, Long sessionDuration){

        // Create an event to upload to Keen.
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("userId", Constants.getuserid());
        event.put("phoneNumber", phoneNumber);
        event.put("app_Version", Constants.getAppVersion());
        event.put("deviceOS", Constants.getdeviceos());
        event.put("timestamp", timestamp);
        event.put("sessionDuration", sessionDuration);
        event.put("sessionID", Constants.getsessionid());
        event.put("yesno", yesno.toUpperCase());

        KeenClient.client().queueEvent(Constants.EVENT_SHARESUCCESS, event);

    }
    public void sharereason(String reason,String phoneNumber, Date timestamp, Long sessionDuration){

        // Create an event to upload to Keen.
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("userId", Constants.getuserid());
        event.put("phoneNumber", phoneNumber);
        event.put("app_Version", Constants.getAppVersion());
        event.put("deviceOS", Constants.getdeviceos());
        event.put("timestamp", timestamp);
        event.put("sessionDuration", sessionDuration);
        event.put("sessionID", Constants.getsessionid());
        event.put("reason", reason.toUpperCase());

        KeenClient.client().queueEvent(Constants.EVENT_SHAREREASON, event);
    }

}
