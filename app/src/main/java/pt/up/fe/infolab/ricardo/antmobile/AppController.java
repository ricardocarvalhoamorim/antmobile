package pt.up.fe.infolab.ricardo.antmobile;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;

public class AppController extends Application {

    private static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty

        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Returns the state of each of the settings in the about fragment
     * @return
     */
    public static boolean[] getStates() {

        SharedPreferences preferences = mInstance.getSharedPreferences(mInstance.getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!preferences.contains("use_data") ||
                !preferences.contains("require_images") ||
                !preferences.contains("require_language")) {

            applySettings(false, true, false);
            return new boolean[]{false, true, false};
        }

        return new boolean[]{
                preferences.getBoolean("use_data", false),
                preferences.getBoolean("require_images", true),
                preferences.getBoolean("require_language", false)};
    }

    /**
     * Saves the received settings to the shared preferences
     * @param data whether data can or not be used to download resources
     * @param pictures display only items with images
     * @param language only display items in english (for now)
     */
    public static void applySettings(boolean data, boolean pictures, boolean language) {

        SharedPreferences preferences = mInstance.getSharedPreferences(mInstance.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();

        if (preferences.contains("use_data") ||
                preferences.contains("require_images") ||
                preferences.contains("require_language")) {
            edit.remove("use_data");
            edit.remove("require_images");
            edit.remove("require_language");
        }

        edit.putBoolean("use_data", data);
        edit.putBoolean("require_images", pictures);
        edit.putBoolean("require_language", language);
        edit.apply();
    }

    /**
     * Enables caching of web requests
     */
    public static void enableHttpResponseCache() {
        try {
            long httpCacheSize = 20 * 1024 * 1024; // 20 MiB
            File httpCacheDir = getInstance().getCacheDir();
            httpCacheDir = new File(httpCacheDir, "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(TAG, "HTTP response cache is unavailable.");
        }
    }
}