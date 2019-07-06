package com.wuujcik.bookadvisor;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**Tag for log messages*/
    private static final String LOG_TAG = BookLoader.class.getName();

    /**Query URL */
    private String mUrl;


    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        // if there's no url or first url is null, return early with null
        if (mUrl == null) {
            return null;
        } else {
            // Perform the HTTP request for books data and process the response.
            List<Book> books = QueryUtils.fetchEarthquakeData(mUrl);
            return books;
        }
    }
}