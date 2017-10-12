package com.kssc0112.hiratemovie.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.kssc0112.hiratemovie.adapters.MovieReviewAdapter;
import com.kssc0112.hiratemovie.Review;
import com.kssc0112.hiratemovie.utilities.Constants;
import com.kssc0112.hiratemovie.utilities.JsonUtils;
import com.kssc0112.hiratemovie.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Loader class for reviews
 */
public class ReviewQueryLoader implements LoaderManager.LoaderCallbacks<Review[]> {
    private static final String TAG = ReviewQueryLoader.class.getSimpleName();
    public static final String QUERY_REVIEW_URL_EXTRA = "query_review";
    private MovieReviewAdapter mMovieReviewAdapter;


    private Review[] mReviewArray;
    private Context mContext;

    public ReviewQueryLoader(Context context, MovieReviewAdapter movieReviewAdapter) {
        mContext = context;
        mMovieReviewAdapter = movieReviewAdapter;
    }

    @Override
    public Loader<Review[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Review[]>(mContext) {
            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (mReviewArray != null) {
                    deliverResult(mReviewArray);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Review[] loadInBackground() {
                if (!NetworkUtils.isOnline(mContext)) {
                    Log.e(TAG, "Network unavailable");
                    return null;
                }

                int urlId = args.getInt(QUERY_REVIEW_URL_EXTRA);

                URL builtUrl = NetworkUtils.buildMovieOtherInfoQueryURL(Constants.REVIEWS, urlId);
                Log.d(TAG, "URL11111: " + builtUrl.toString());

                String jsonResponse;
                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
                } catch (IOException e) {
                    Log.e(TAG, "HTTP Exception Occured");
                    e.printStackTrace();
                    return null;
                }

                Review[] parsedMovieReviewJsonData = null;

                if (jsonResponse != null) {
                    try {
                        parsedMovieReviewJsonData = JsonUtils.getReviewInfoFromJson(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception Occured");
                        e.printStackTrace();
                        return null;
                    }
                }
                return parsedMovieReviewJsonData;
            }

            @Override
            public void deliverResult(Review[] data) {
                mReviewArray = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Review[]> loader, Review[] data) {
        if (data == null) {
            Log.i(TAG, "No Movie Review Available");
            return;
        }
        mMovieReviewAdapter.setMovieReviewList(data);
    }

    @Override
    public void onLoaderReset(Loader<Review[]> loader) {

    }
}
