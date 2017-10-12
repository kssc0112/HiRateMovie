package com.kssc0112.hiratemovie.utilities;

import android.text.TextUtils;

import com.kssc0112.hiratemovie.Movie;
import com.kssc0112.hiratemovie.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * JsonUtils
 */
public class JsonUtils {
    private final static String TAG = JsonUtils.class.getSimpleName();

    private final static String TITLE = "title";
    private final static String RESULT = "results";
    private final static String POSTER_PATH = "poster_path";
    private final static String OVERVIEW = "overview";
    private final static String RELEASE_DATE = "release_date";
    private final static String USER_RATING = "vote_average";
    private final static String ID = "id";

    private final static String KEY = "key";
    private final static String TRAILER_SITE = "site";

    private final static String AUTHOR = "author";
    private final static String CONTENT = "content";

    /**
     * parse Json into movie objects
     *
     * @param movieInfoJsonStr
     * @return
     * @throws JSONException
     */
    public static Movie[] getMovieInfoFromJson(String movieInfoJsonStr) throws JSONException {
        Movie[] parsedMovieInfoArray;
        JSONObject movieJson = new JSONObject(movieInfoJsonStr);
        JSONArray movieInfoArray = movieJson.getJSONArray(RESULT);
        parsedMovieInfoArray = new Movie[movieInfoArray.length()];

        for (int i = 0; i < movieInfoArray.length(); i++) {

            String title;
            String posterPath;
            String overview;
            String releaseDate;
            String userRating;
            int id;

            JSONObject singleResultObject = movieInfoArray.getJSONObject(i);
            id = singleResultObject.getInt(ID);
            title = singleResultObject.getString(TITLE);
            posterPath = singleResultObject.getString(POSTER_PATH);
            overview = singleResultObject.getString(OVERVIEW);
            releaseDate = singleResultObject.getString(RELEASE_DATE);
            userRating = singleResultObject.getString(USER_RATING);

            parsedMovieInfoArray[i] = new Movie(id, title, posterPath, overview, userRating, releaseDate);
        }

        return parsedMovieInfoArray;
    }

    public static String[] getTrailerInfoFromJson(String trailerInfoJsonStr) throws JSONException {
        String[] parsedTrailerInfoArray;
        JSONObject trailerJson = new JSONObject(trailerInfoJsonStr);
        JSONArray trailerInfoArray = trailerJson.getJSONArray(RESULT);
        parsedTrailerInfoArray = new String[trailerInfoArray.length()];

        int youtubeTrailerIndex = 0;
        for (int i = 0; i < trailerInfoArray.length(); i++) {
            JSONObject singleResultObject = trailerInfoArray.getJSONObject(i);
            String key = singleResultObject.getString(KEY);
            String trailerSite = singleResultObject.getString(TRAILER_SITE);
            if (trailerSite.equalsIgnoreCase("youtube")) {
                parsedTrailerInfoArray[youtubeTrailerIndex++] = key;
            }
        }

        return parsedTrailerInfoArray;
    }

    public static Review[] getReviewInfoFromJson(String reviewInfoJsonStr) throws JSONException {
        Review[] parsedReviewInfoArray;
        JSONObject reviewJson = new JSONObject(reviewInfoJsonStr);
        JSONArray reviewInfoArray = reviewJson.getJSONArray(RESULT);
        parsedReviewInfoArray = new Review[reviewInfoArray.length()];

        for (int i = 0; i < reviewInfoArray.length(); i++) {
            JSONObject singleResultObject = reviewInfoArray.getJSONObject(i);
            String id = singleResultObject.getString(ID);
            String author = singleResultObject.getString(AUTHOR);
            String content = singleResultObject.getString(CONTENT);

            //cut off at 501 chars
            if (!TextUtils.isEmpty(content) && content.length() > 500) {
                content = content.substring(0, 502);
            }

            parsedReviewInfoArray[i] = new Review(id, author, content);
        }

        return parsedReviewInfoArray;
    }

    /**
     * extract a movie info from JSONObject
     * @param movieInfo
     * @return
     * @throws JSONException
     */
    public static Movie getMovie(String movieInfo) throws JSONException {
        JSONObject movieJson = new JSONObject(movieInfo);
        int id = movieJson.getInt(ID);
        String title = movieJson.getString(TITLE);
        String posterPath = movieJson.getString(POSTER_PATH);
        String overview = movieJson.getString(OVERVIEW);
        String releaseDate = movieJson.getString(RELEASE_DATE);
        String userRating = movieJson.getString(USER_RATING);

        Movie movie = new Movie(id, title, posterPath, overview, userRating, releaseDate);

        return movie;
    }
}
