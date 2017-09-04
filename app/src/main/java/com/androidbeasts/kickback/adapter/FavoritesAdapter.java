package com.androidbeasts.kickback.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidbeasts.kickback.R;
import com.androidbeasts.kickback.data.FavoritesContract;
import com.squareup.picasso.Picasso;

public class FavoritesAdapter extends CursorRecyclerViewAdapter<FavoritesAdapter.ViewHolder> {

    private static final String LOG_TAG = FavoritesAdapter.class.getSimpleName();
    private Context mContext;
//    private static int sLoaderID;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private FavoritesAdapter.ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public FavoritesAdapter(Context context, Cursor c, FavoritesAdapter.ListItemClickListener listItemClickListener) {
        super(context, c);
        mContext = context;
        mOnClickListener = listItemClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieNameTv, movieRatingTv;
        ImageView movieImage;

        public ViewHolder(View view) {
            super(view);
            movieNameTv = (TextView) view.findViewById(R.id.movie_name);
            movieRatingTv = (TextView) view.findViewById(R.id.rating);
            movieImage = (ImageView) view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        /**
         * A method we wrote for convenience.
         *
         * @param cursor Position of the item in the list
         */
        void bind(Cursor cursor) {
            final String image = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_IMAGE));
            final String name = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_NAME));
            final String rating = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_RATING));

            movieNameTv.setText(name);
            movieRatingTv.setText(rating);
            Picasso.with(mContext).load(image).placeholder(R.drawable.movie_icon).error(R.drawable.movie_icon).into(movieImage);
        }
    }

    /*@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_grid_item;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "In bind View");
    }*/
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.movie_grid_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

}
