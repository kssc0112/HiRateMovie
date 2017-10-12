package com.kssc0112.hiratemovie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kssc0112.hiratemovie.adapters.MoviePosterAdapter;
import com.kssc0112.hiratemovie.utilities.Constants;
import com.kssc0112.hiratemovie.utilities.JsonUtils;
import com.kssc0112.hiratemovie.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity implements MoviePosterAdapter
        .MoviePosterAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Movie[]> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_QUERY_LOADER = 23;
    private static final String QUERY_MOVIES_URL_EXTRA = "query_movies";

    private RecyclerView mRecyclerView;
    private TextView mNoContentTextView;
    private MoviePosterAdapter mMoviePosterAdapter;
    private ProgressBar mProgressBar;
    private Context mContext;

    /**
     * Initialize the recyclerView and start the background query task
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.movie_poster_recyclerView);
        mNoContentTextView = (TextView) findViewById(R.id.no_content_textView);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
        mMoviePosterAdapter = new MoviePosterAdapter(this);
        mRecyclerView.setAdapter(mMoviePosterAdapter);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_MOVIES_URL_EXTRA, Constants.POPULAR);
        getSupportLoaderManager().initLoader(MOVIE_QUERY_LOADER, queryBundle, this);
        int currentOrientation = getResources().getConfiguration().orientation;

        GridLayoutManager gridLayoutManager;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, Constants.NUMBER_OF_COLUMN_PORTRAIT);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(this, Constants.NUMBER_OF_COLUMN_LANDSCAPE);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    /**
     * When the poster is clicked, user is sent to the detailed activity
     *
     * @param movie
     */
    @Override
    public void OnPosterClick(Movie movie) {
        Intent toDetailPageIntent = new Intent(this, DetailScreenActivity.class);
        toDetailPageIntent.putExtra("movie", movie);
        startActivity(toDetailPageIntent);
    }

    /**
     * Add custom menu to the bar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.by_popularity:
                makePosterQueryTask(Constants.POPULAR);
                break;
            case R.id.by_rating:
                makePosterQueryTask(Constants.TOP_RATED);
                break;
            case R.id.favorite:
                makePosterQueryTask(Constants.FAVORITE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void makePosterQueryTask(String param) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_MOVIES_URL_EXTRA, param);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_QUERY_LOADER);

        if (movieSearchLoader == null) {
            loaderManager.initLoader(MOVIE_QUERY_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_QUERY_LOADER, queryBundle, this);
        }

    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie[]>(this) {
            private Movie[] mMovieArray;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);
                String urlString = args.getString(QUERY_MOVIES_URL_EXTRA);

                if (mMovieArray != null
                        && urlString != null
                        && !urlString.equalsIgnoreCase(Constants.FAVORITE)) {
                    deliverResult(mMovieArray);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Movie[] loadInBackground() {

                if (!NetworkUtils.isOnline(getApplicationContext())) {
                    Log.e(TAG, "Network unavailable");
                    return null;
                }

                String urlString = args.getString(QUERY_MOVIES_URL_EXTRA);
                if (TextUtils.isEmpty(urlString)) {
                    return null;
                }

                Movie[] parsedJsonData = null;

                if (urlString.equalsIgnoreCase(Constants.FAVORITE)) {
                    parsedJsonData = NetworkUtils.getFavoriteMovies(mContext);
                    if (parsedJsonData != null) {
                        String message;
                        if (parsedJsonData.length < 1) {
                            message = "No favorite movie available!";
                            Log.d(TAG, "Favorite: " + message);
                        } else {
                            message = "successfully loaded!";
                            Log.d(TAG, "Favorite: " + message);
                            return parsedJsonData;
                        }
                    }
                    return null;
                }
                URL builtUrl = NetworkUtils.buildMovieInfoQueryURL(urlString);
                String jsonResponse;
                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
                } catch (IOException e) {
                    Log.e(TAG, "HTTP Exception Occured");
                    e.printStackTrace();
                    return null;
                }

                if (jsonResponse != null) {
                    try {
                        parsedJsonData = JsonUtils.getMovieInfoFromJson(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception Occured");
                        e.printStackTrace();
                        return null;
                    }
                }
                return parsedJsonData;

            }

            @Override
            public void deliverResult(Movie[] movies) {
                Log.d(TAG, "In deliverResult");
                mMovieArray = movies;
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setVisibility(View.GONE);
                }
                if (movies == null) {
                    showContent(false);
                } else if (movies.length < 1) {
                    showContent(false);
                }
                showContent(true);
                mMoviePosterAdapter.setMoviePosterURLList(movies);
                super.deliverResult(movies);
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] movies) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (movies == null) {
            Log.i(TAG, "No Movie Available");
            showContent(false);
            return;
        } else if (movies.length < 1) {
            showContent(false);
        }
        showContent(true);
        mMoviePosterAdapter.setMoviePosterURLList(movies);
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    private void showContent(boolean IsMovieAvailable) {
        if (IsMovieAvailable) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoContentTextView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoContentTextView.setVisibility(View.VISIBLE);
        }
    }
}
