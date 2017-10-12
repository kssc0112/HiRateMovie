package com.kssc0112.hiratemovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kssc0112.hiratemovie.R;
import com.kssc0112.hiratemovie.utilities.NetworkUtils;

/**
 * Movie Trailer Adapter
 */
public class MovieTrailerAdapter extends
        RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {
    private static final String TAG = MovieTrailerAdapter.class.getSimpleName();

    private Context mContext;
    private String[] mTrailerList;

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        holder.mImageView.setImageResource(R.drawable.play_icon);
        String trailerString = mContext.getString(R.string.trailer) + " " + (position + 1);
        holder.mTextView.setText(trailerString);
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) {
            Log.d(TAG, "MovieTrailerAdapter is NULL");
            return 0;
        }
        return mTrailerList.length;
    }


    public void setMovieTrailerURLList(String[] trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }


     public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImageView;
        private final TextView mTextView;
        private final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.trailer_image);
            mTextView = (TextView) itemView.findViewById(R.id.trailer_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String url = YOUTUBE_BASE_URL + mTrailerList[position];
            NetworkUtils.openWebPage(mContext, url);
        }
    }
}
