package com.androidbeasts.kickback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidbeasts.kickback.R;
import com.androidbeasts.kickback.model.Trailer;

import java.util.ArrayList;

/*Adapter class for list of trailers*/
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private int numberOfItems;
    private ArrayList<Trailer> trailerArrayList;
    private Context context;

    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public TrailerAdapter(int numberOfItems, ArrayList<Trailer> trailerArrayList, Context context, TrailerAdapter.ListItemClickListener listener) {
        this.trailerArrayList = new ArrayList<>();
        this.numberOfItems = numberOfItems;
        this.trailerArrayList = trailerArrayList;
        this.context = context;
        mOnClickListener = listener;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.trailer_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        // Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberOfItems;
    }

    class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trailerNameTv;

        public TrailerHolder(View itemView) {
            super(itemView);
            trailerNameTv = (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            Trailer trailer = trailerArrayList.get(listIndex);
            trailerNameTv.setText(trailer.getTrailerName());
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
