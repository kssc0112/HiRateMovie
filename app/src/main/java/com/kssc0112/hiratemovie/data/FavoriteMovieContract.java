package com.kssc0112.hiratemovie.data;

import android.net.Uri;

/**
 * FavoriteMovieContract
 */
public class FavoriteMovieContract {
    private static final String TAG = FavoriteMovieContract.class.getSimpleName();
    public static final String AUTHORITY = "com.kssc0112.hiratemovie.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    /* FavoriteMovieEntry is an inner class that defines the contents of the favorites table */
    public static final class FavoriteMovieEntry {

        // FavoriteMovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // Task table and column names
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID= "movie_id";


        /*

        favorites
         - - - - - - - - -
        |     movie_id    |
         - - - - - - - - -
        |       1234      |
         - - - - - - - - -
        |       5324      |
         - - - - - - - - -
        .
        .
        .
         - - - - - - - - -
        |       ...      |
         - - - - - - - - -

         */

    }
}

