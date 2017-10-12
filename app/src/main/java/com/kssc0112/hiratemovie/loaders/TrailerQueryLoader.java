package com.kssc0112.hiratemovie.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.kssc0112.hiratemovie.adapters.MovieTrailerAdapter;
import com.kssc0112.hiratemovie.utilities.Constants;
import com.kssc0112.hiratemovie.utilities.JsonUtils;
import com.kssc0112.hiratemovie.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Loader class for trailer
 */
public class TrailerQueryLoader implements LoaderManager.LoaderCallbacks<String[]> {
    private static final String TAG = TrailerQueryLoader.class.getSimpleName();
    public static final String QUERY_TRAILER_URL_EXTRA = "query_trailer";
    private MovieTrailerAdapter mMovieTrailerAdapter;


    private String[] mTrailerArray;
    private Context mContext;

    public TrailerQueryLoader(Context context, MovieTrailerAdapter movieTrailerAdapter) {
        mContext = context;
        mMovieTrailerAdapter = movieTrailerAdapter;
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(mContext) {
            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (mTrailerArray != null) {
                    deliverResult(mTrailerArray);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                if (!NetworkUtils.isOnline(mContext)) {
                    Log.e(TAG, "Network unavailable");
                    return null;
                }

                int urlId = args.getInt(QUERY_TRAILER_URL_EXTRA);

                URL builtUrl = NetworkUtils.buildMovieOtherInfoQueryURL(Constants.VIDEOS, urlId);
                Log.d(TAG, "URL11111: " + builtUrl.toString());

                String jsonResponse;
                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
                } catch (IOException e) {
                    Log.e(TAG, "HTTP Exception Occured");
                    e.printStackTrace();
                    return null;
                }

                String[] parsedJsonData = null;

                if (jsonResponse != null) {
                    try {
                        parsedJsonData = JsonUtils.getTrailerInfoFromJson(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception Occured");
                        e.printStackTrace();
                        return null;
                    }
                }
                return parsedJsonData;
            }

            @Override
            public void deliverResult(String[] data) {
                mTrailerArray = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        if (data == null) {
            Log.i(TAG, "No Movie Trailer Available");
            return;
        }
        mMovieTrailerAdapter.setMovieTrailerURLList(data);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}
