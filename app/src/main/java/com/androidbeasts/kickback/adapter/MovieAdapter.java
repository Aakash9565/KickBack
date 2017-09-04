package com.androidbeasts.kickback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidbeasts.kickback.R;
import com.androidbeasts.kickback.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*Adapter class for list of movies*/
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private int numberOfItems;
    private ArrayList<Movie> movieArrayList;
    private Context context;
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MovieAdapter(int numberOfItems, ArrayList<Movie> movieArrayList, Context context, ListItemClickListener listener) {
        this.movieArrayList = new ArrayList<>();
        this.numberOfItems = numberOfItems;
        this.movieArrayList = movieArrayList;
        this.context = context;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.movie_grid_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
       // Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberOfItems;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieNameTv, movieRatingTv;
        ImageView movieImage;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieNameTv = (TextView) itemView.findViewById(R.id.movie_name);
            movieRatingTv = (TextView) itemView.findViewById(R.id.rating);
            movieImage = (ImageView) itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            Movie movie = movieArrayList.get(listIndex);
            movieNameTv.setText(movie.getMovieName());
            movieRatingTv.setText(movie.getMovieRating());
            Picasso.with(context).load(movie.getMovieImage()).placeholder(R.drawable.movie_icon).error(R.drawable.movie_icon).into(movieImage);
        }


        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
