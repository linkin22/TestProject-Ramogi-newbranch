package com.ramogi.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;


/**
 * Created by ROchola on 2/7/2015.
 * The class the downloads images from the given url asynchronously
 *
 */
public class ImageDownloadOne extends AsyncTask<Void, Void, Drawable> {

    private Context context;
    private CallBackImageOne callBackImage ;
    private Drawable d;
    private String urlImage;

    //constructor
    public ImageDownloadOne(String urlImage, CallBackImageOne cbi, Context context) {

        this.callBackImage = cbi;
        this.context = context;
        this.urlImage = urlImage;
    }

    @Override
    protected Drawable doInBackground(Void... params) {


            try {
                Log.v("image download try ", "url being downloaded " + urlImage);
                InputStream is = new URL(urlImage.trim()).openStream();
                d = Drawable.createFromStream(is, "src name");
                is.close();
            } catch (Exception e) {
                Log.v("image download catch ", "url not downloaded " + urlImage);
                d = context.getResources().getDrawable(R.drawable.ruth);
            }
        return d;
    }

    @Override
    protected void onPostExecute(Drawable images) {

        callBackImage.querycomplete(images);

    }

}
