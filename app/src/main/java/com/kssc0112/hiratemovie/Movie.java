package com.kssc0112.hiratemovie;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie
 */
public class Movie implements Parcelable {
    private static final String TAG = Movie.class.getSimpleName();
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342";

    private int mId;
    private String mTitle;
    private String mImageThumbNail;
    private String mOverView;
    private String mRating;
    private String mReleasedDate;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mImageThumbNail);
        dest.writeString(mOverView);
        dest.writeString(mRating);
        dest.writeString(mReleasedDate);
    }
    /**
     * @param id
     * @param title
     * @param imageThumbNail
     * @param overView
     * @param rating
     * @param releaseDate
     */
    public Movie(int id, String title, String imageThumbNail, String overView,
                 String rating, String releaseDate) {
        mId = id;
        mTitle = title;
        mImageThumbNail = BASE_IMAGE_URL + imageThumbNail;
        mOverView = overView;
        mRating = rating;
        mReleasedDate = releaseDate;
    }

    private Movie (Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mImageThumbNail = in.readString();
        mOverView = in.readString();
        mRating = in.readString();
        mReleasedDate = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() { return mId; }
    /**
     * get title
     *
     * @return
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * get imageThumbNail
     *
     * @return
     */
    public String getImageThumbNail() {
        return mImageThumbNail;
    }

    /**
     * get overView
     *
     * @return
     */
    public String getOverView() {
        return mOverView;
    }

    /**
     * get rating
     *
     * @return
     */
    public String getRating() {
        return mRating;
    }

    /**
     * get releasedDate
     *
     * @return
     */
    public String getReleasedDate() {
        return mReleasedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
