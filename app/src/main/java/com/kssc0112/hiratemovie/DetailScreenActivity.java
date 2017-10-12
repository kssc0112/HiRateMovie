package com.kssc0112.hiratemovie;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kssc0112.hiratemovie.adapters.MovieReviewAdapter;
import com.kssc0112.hiratemovie.adapters.MovieTrailerAdapter;
import com.kssc0112.hiratemovie.data.FavoriteMovieContract;
import com.kssc0112.hiratemovie.loaders.ReviewQueryLoader;
import com.kssc0112.hiratemovie.loaders.TrailerQueryLoader;
import com.squareup.picasso.Picasso;

import static com.kssc0112.hiratemovie.loaders.ReviewQueryLoader.QUERY_REVIEW_URL_EXTRA;
import static com.kssc0112.hiratemovie.loaders.TrailerQueryLoader.QUERY_TRAILER_URL_EXTRA;

/**
 * Detailed Screen Activity
 */
public class DetailScreenActivity extends AppCompatActivity {
    private static final String TAG = DetailScreenActivity.class.getSimpleName();
    private static final int TRAILER_QUERY_LOADER = 1234;
    private static final int REVIEW_QUERY_LOADER = 1235;

    private TextView mTitleTextView;
    private TextView mOverViewTextView;
    private TextView mReleasedDateTextView;
    private TextView mUserRatingTextView;
    private ImageView mMoviePosterImageView;
    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewRecyclerView;
    private Button mFavoriteButton;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private Context mContext;

    private LoaderManager.LoaderCallbacks<String[]> mTrailerQueryLoader;
    private MovieTrailerAdapter mMovieTrailerAdapter;
    private LoaderManager.LoaderCallbacks<Review[]> mReviewQueryLoader;
    private MovieReviewAdapter mMovieReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_detail_screen);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mOverViewTextView = (TextView) findViewById(R.id.overview);
        mReleasedDateTextView = (TextView) findViewById(R.id.release_date);
        mMoviePosterImageView = (ImageView) findViewById(R.id.poster);
        mUserRatingTextView = (TextView) findViewById(R.id.user_rating);
        mFavoriteButton = (Button) findViewById(R.id.favorite_button);
        Bundle movieData = getIntent().getExtras();
        Movie movie = movieData.getParcelable("movie");
        initLoaders();
        mMovieTrailerAdapter = new MovieTrailerAdapter();
        initLinearRecyclerView(mMovieTrailerAdapter);
        mMovieReviewAdapter = new MovieReviewAdapter();
        initLinearRecyclerView(mMovieReviewAdapter);
        initLoaders();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        if (movie != null) {
            final int id = movie.getId();
            updateFavoriteButton(mFavoriteButton, getFavorite(id));
            String title = movie.getTitle();
            String overView = movie.getOverView();
            String releaseDate = "Released Date : " + movie.getReleasedDate();
            String moviePosterURL = movie.getImageThumbNail();
            Log.d(TAG, "PosterURL: " + moviePosterURL);
            String userRating = "Average Rating: " + movie.getRating() + "/10";
            mTitleTextView.setText(title);
            mOverViewTextView.setText(overView);
            mReleasedDateTextView.setText(releaseDate);
            mUserRatingTextView.setText(userRating);
            Picasso.with(this).load(moviePosterURL).into(mMoviePosterImageView);
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message;
                    if (!favoriteExist(id)) {
                        markAsFavorite(id);
                        setFavorite(id, true);
                        updateFavoriteButton(mFavoriteButton, true);
                        message = "marked as favorite :)";
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    } else {
                        removeFromFavorite(id);
                        message = "removed from favorite :(";
                        setFavorite(id, false);
                        updateFavoriteButton(mFavoriteButton, false);
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Bundle trailerQueryBundle = new Bundle();
            trailerQueryBundle.putInt(QUERY_TRAILER_URL_EXTRA, id);
            getSupportLoaderManager().initLoader(TRAILER_QUERY_LOADER,
                    trailerQueryBundle, mTrailerQueryLoader);
            Bundle reviewQueryBundle = new Bundle();
            reviewQueryBundle.putInt(QUERY_REVIEW_URL_EXTRA, id);
            getSupportLoaderManager().initLoader(REVIEW_QUERY_LOADER,
                    reviewQueryBundle, mReviewQueryLoader);
        }
    }

    /**
     * marks the move as favorite
     * @param movieId
     */
    private void markAsFavorite(int movieId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movieId);
        getContentResolver().insert(
                FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
    }

    /**
     * check if a movie already exists in favorite movie db
     * @param movieId
     * @return
     */
    public boolean favoriteExist(int movieId) {
        boolean isMoviePresentInDb = false;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                    null,
                    FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = " + movieId,
                    null,
                    null);
            isMoviePresentInDb = cursor.moveToNext();
        } catch (Exception e) {
            Log.w(TAG, "Failed processing favoriteExist");
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isMoviePresentInDb;
    }

    /**
     * initialize the recycler view
     * @param adapter
     */
    private void initLinearRecyclerView(RecyclerView.Adapter adapter) {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(mContext,
                R.drawable.custom_divider));
        if (adapter instanceof MovieTrailerAdapter) {
            mTrailerRecyclerView = (RecyclerView) findViewById(R.id.trailers_recyclerView);
            mTrailerRecyclerView.setLayoutManager(layoutManager);
            mTrailerRecyclerView.addItemDecoration(dividerItemDecoration);
            mTrailerRecyclerView.setAdapter(mMovieTrailerAdapter);
        } else if (adapter instanceof MovieReviewAdapter) {
            mReviewRecyclerView = (RecyclerView) findViewById(R.id.reviews_recyclerView);
            mReviewRecyclerView.setLayoutManager(layoutManager);
            mReviewRecyclerView.addItemDecoration(dividerItemDecoration);
            mReviewRecyclerView.setAdapter(mMovieReviewAdapter);
        }
    }

    /**
     * initialize the loaders
     */
    private void initLoaders() {
        mTrailerQueryLoader = new TrailerQueryLoader(this, mMovieTrailerAdapter);
        mReviewQueryLoader = new ReviewQueryLoader(this, mMovieReviewAdapter);

    }

    /**
     * store in sharedPreference
     * @param movieId
     * @param flag
     */
    private void setFavorite(int movieId, boolean flag) {
        String movieIdString = String.valueOf(movieId);
        mEditor.putBoolean(movieIdString, flag);
        mEditor.commit();
    }

    /**
     * check users previous preference
     * @param movieId
     * @return
     */
    private boolean getFavorite(int movieId) {
        String movieIdString = String.valueOf(movieId);
        boolean isFavorite = mSharedPreferences.getBoolean(movieIdString, false);
        return isFavorite;
    }

    /**
     * remove from favorite
     * @param movieId
     */
    private void removeFromFavorite(int movieId) {
        String movieIdString = String.valueOf(movieId);
        Uri deleteUri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(movieIdString)
                .build();
        getContentResolver().delete(
                deleteUri,
                null,
                null);
    }

    /**
     * update the favorite button
     * @param textView
     * @param isFavorite
     */
    private void updateFavoriteButton(TextView textView, boolean isFavorite) {
        if (!isFavorite) {
            textView.setText(getString(R.string.mark_favorite));
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            textView.setText(getString(R.string.remove_favorite));
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
        }
    }
}
