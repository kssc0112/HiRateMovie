package com.kssc0112.hiratemovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB helper
 */
public class FavoriteMovieDbHelper extends SQLiteOpenHelper {
    private static final String TAG = FavoriteMovieDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "favoriteMoviesDb.db";
    private static final int VERSION = 2;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " +
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY);";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}