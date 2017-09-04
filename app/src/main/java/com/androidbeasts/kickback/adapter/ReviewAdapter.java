package com.androidbeasts.kickback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidbeasts.kickback.R;
import com.androidbeasts.kickback.model.Review;

import java.util.ArrayList;

/*Adapter class for list of reviews*/
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private int numberOfItems;
    private ArrayList<Review> reviewArrayList;
    private Context context;

    public ReviewAdapter(int numberOfItems, ArrayList<Review> reviewArrayList, Context context) {
        this.reviewArrayList = new ArrayList<>();
        this.numberOfItems = numberOfItems;
        this.reviewArrayList = reviewArrayList;
        this.context = context;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.review_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberOfItems;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        TextView reviewAuthorTv, reviewContentTv;

        public ReviewHolder(View itemView) {
            super(itemView);
            reviewAuthorTv = (TextView) itemView.findViewById(R.id.review_author);
            reviewContentTv = (TextView) itemView.findViewById(R.id.review_content);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            Review review = reviewArrayList.get(listIndex);
            reviewAuthorTv.setText(review.getAuthor());
            reviewContentTv.setText(review.getContent());
        }
    }
}
