package com.kssc0112.hiratemovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kssc0112.hiratemovie.Movie;
import com.kssc0112.hiratemovie.R;
import com.squareup.picasso.Picasso;

/**
 * Movie Poster Adapter
 */
public class MoviePosterAdapter
        extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterViewHolder> {
    private static final String TAG = MoviePosterAdapter.class.getSimpleName();

    private Movie[] mMovieList;
    private Context mContext;
    private MoviePosterAdapterOnClickHandler mOnClickHandler;

    public MoviePosterAdapter(MoviePosterAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        String moviePosterURL = mMovieList[position].getImageThumbNail();

        Picasso.with(mContext).load(moviePosterURL)
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.error_img)
                .into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null){
            Log.d(TAG, "List is NULL");
            return 0;
        }
        return mMovieList.length;
    }

    public void setMoviePosterURLList(Movie[] moviePosterURLList) {
        mMovieList = moviePosterURLList;
        notifyDataSetChanged();
    }

    public interface MoviePosterAdapterOnClickHandler {
        void OnPosterClick(Movie movie);
    }

    public class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMoviePosterImageView;

        public MoviePosterViewHolder(View itemView) {
            super(itemView);
            mMoviePosterImageView = (ImageView) itemView.findViewById(R.id.movie_poster_imageView);
            mMoviePosterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentIndex = getAdapterPosition();
            mOnClickHandler.OnPosterClick(mMovieList[currentIndex]);
        }
    }
}
