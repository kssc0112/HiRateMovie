package com.kssc0112.hiratemovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kssc0112.hiratemovie.R;
import com.kssc0112.hiratemovie.Review;
import com.kssc0112.hiratemovie.utilities.NetworkUtils;

/**
 * Movie Review Adapter
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {
    private static final String TAG = MovieReviewAdapter.class.getSimpleName();
    private static final String REVIEW_BASE_URL = "https://www.themoviedb.org/review/";

    private Context mContext;
    private Review[] mReviewList;

    public  MovieReviewAdapter() {

    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        String author = "A review by " + mReviewList[position].getAuthor();
        String content = mReviewList[position].getContent();
        if (content.length() > 500) {
            content += "...";
        }
        final String url = REVIEW_BASE_URL + mReviewList[position].getId();
        holder.mAuthorTextView.setText(author);
        holder.mContentTextView.setText(content);
        holder.mUrlTextView.setText(mContext.getString(R.string.view_full_review));
        holder.mUrlTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtils.openWebPage(mContext, url);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) {

            Log.d(TAG, "MovieReviewAdapter is NULL");
            return 0;
        }
        return mReviewList.length;
    }

    public void setMovieReviewList(Review[] reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView mAuthorTextView;
        private final TextView mContentTextView;
        private final TextView mUrlTextView;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.review_author);
            mContentTextView = (TextView) itemView.findViewById(R.id.review_content);
            mUrlTextView = (TextView) itemView.findViewById(R.id.review_url);
        }
    }
}
