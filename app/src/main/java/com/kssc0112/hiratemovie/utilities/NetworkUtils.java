package com.kssc0112.hiratemovie.utilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.kssc0112.hiratemovie.Movie;
import com.kssc0112.hiratemovie.data.FavoriteMovieContract;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Network Utilities
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();


    /**
     * returns a array of movies with supplied movie ID
     * @param context
     * @return
     */
    public static Movie[] getFavoriteMovies(Context context) {
        Uri favoriteMovieUri = FavoriteMovieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(FavoriteMovieContract.PATH_FAVORITES)
                .build();
        Cursor favoriteMovieCursor = context.getContentResolver()
                .query(favoriteMovieUri,
                        null,
                        null,
                        null,
                        null);

        if (favoriteMovieCursor == null) {
            Log.d(TAG, "No favorite movies available");
            return null;
        } else if (favoriteMovieCursor.getColumnCount() < 1) {
            Log.d(TAG, "No favorite movies available");
            favoriteMovieCursor.close();
            return null;

        }

        List<Movie> favoriteMovieList = new ArrayList<>();

        try {
            while (favoriteMovieCursor.moveToNext()) {
                String movie_id = String.valueOf(favoriteMovieCursor.getInt(0));
                Uri getMovieUri = Uri.parse(Constants.BASE_URL).buildUpon()
                        .appendPath(movie_id)
                        .appendQueryParameter(Constants.API_PARAM, Constants.MY_API_KEY)
                        .build();
                URL url = null;

                try {
                    url = new URL(getMovieUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                String jsonResponse;
                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    Log.e(TAG, "HTTP Exception Occured");
                    e.printStackTrace();
                    return null;
                }
                try {
                    Movie favoriteMovie = JsonUtils.getMovie(jsonResponse);
                    favoriteMovieList.add(favoriteMovie);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception Occured");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while processing Cursor");
            e.printStackTrace();
        } finally {
            favoriteMovieCursor.close();
        }

        Movie[] favoriteMovieArray = favoriteMovieList.toArray(new Movie[0]);
        return favoriteMovieArray;

    }
    /**
     * build URL with corresponding param to query
     *
     * @param key
     * @return
     */
    public static URL buildMovieInfoQueryURL(String key) {
        String BASE_URL = null;
        switch (key) {
            case Constants.POPULAR:
                BASE_URL = Constants.BASE_POPULAR_MOVIE_DB_URL;
                break;
            case Constants.TOP_RATED:
                BASE_URL = Constants.BASE_TOP_MOVIE_DB_URL;
                break;
        }

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(Constants.API_PARAM, Constants.MY_API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildMovieOtherInfoQueryURL(String key, int id) {
        String info = null;
        switch (key) {
            case Constants.REVIEWS:
                info = Constants.REVIEWS;
                break;
            case Constants.VIDEOS:
                info = Constants.VIDEOS;
                break;
        }

        String idString = String.valueOf(id);

        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendPath(idString)
                .appendPath(info)
                .appendQueryParameter(Constants.API_PARAM, Constants.MY_API_KEY)
                .build();

        Log.d(TAG, "11111URI: " + builtUri.toString());

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Built URI " + url);
        return url;

    }

    /**
     * reads the response from Http request
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * check whether if user is on a network
     *
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void openWebPage(Context context, String url) {
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent.createChooser(intent, "Pick Your Favorite Browser"));
        }
    }

}
