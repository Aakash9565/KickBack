package com.androidbeasts.kickback;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbeasts.kickback.adapter.ReviewAdapter;
import com.androidbeasts.kickback.adapter.TrailerAdapter;
import com.androidbeasts.kickback.data.FavoritesContract;
import com.androidbeasts.kickback.model.Movie;
import com.androidbeasts.kickback.model.Review;
import com.androidbeasts.kickback.model.Trailer;
import com.androidbeasts.kickback.utils.ConnectionUtil;
import com.androidbeasts.kickback.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*This activity shows the details of a movie including movie poster,movie name, rating, release date and overview*/
public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener, View.OnClickListener {

    private final String TRAILER_LIST_PARCELABLE_STRING = "trailers";
    private final String REVIEW_LIST_PARCELABLE_STRING = "trailers";
    private final String MOVIE_PARCELABLE_STRING = "movie";
    private final String TAG = this.getClass().getSimpleName();

    private TrailerAdapter trailerAdapter;
    private RecyclerView mTrailersList;
    private ArrayList<Trailer> trailerArrayList;
    private ProgressBar mProgressBar;
    private ProgressBar mReviewsProgressBar;

    private ReviewAdapter reviewAdapter;
    private RecyclerView mReviewsList;
    private ArrayList<Review> reviewArrayList;
    private BottomSheetBehavior mBottomSheetBehavior;

    private String NO_INTERNET_MESSAGE;
    private ConnectionUtil connectionUtil;
    private String movieId;

    private Button mFavoriteButton;
    private Button mReviewsButton;

    private Movie movie;

    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    private Toast mToast;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (trailerArrayList != null) {
            outState.putParcelableArrayList(TRAILER_LIST_PARCELABLE_STRING, trailerArrayList);
        }
        if (reviewArrayList != null) {
            outState.putParcelableArrayList(REVIEW_LIST_PARCELABLE_STRING, reviewArrayList);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        connectionUtil = new ConnectionUtil(this);
        NO_INTERNET_MESSAGE = getResources().getString(R.string.no_internet_message);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mReviewsProgressBar = (ProgressBar) findViewById(R.id.reviews_progressBar);
        mTrailersList = (RecyclerView) findViewById(R.id.trailers_recyclerview);
        mReviewsList = (RecyclerView) findViewById(R.id.reviews_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mTrailersList.setLayoutManager(linearLayoutManager);
        mTrailersList.setHasFixedSize(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mTrailersList.setNestedScrollingEnabled(false);
            mReviewsList.setNestedScrollingEnabled(false);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mReviewsList = (RecyclerView) findViewById(R.id.reviews_recyclerview);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager1.setAutoMeasureEnabled(true);
        mReviewsList.setLayoutManager(linearLayoutManager1);
        mReviewsList.setHasFixedSize(true);

        mFavoriteButton = (Button) findViewById(R.id.favorite_button);
        mReviewsButton = (Button) findViewById(R.id.see_reviews_button);
        mFavoriteButton.setOnClickListener(this);
        mReviewsButton.setOnClickListener(this);

        ImageView movieImage = (ImageView) findViewById(R.id.movie_big_image);
        TextView movie_title = (TextView) findViewById(R.id.title);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView overview = (TextView) findViewById(R.id.overview);
        TextView release_date = (TextView) findViewById(R.id.release_date);
        Bundle bundle = getIntent().getExtras();
        movie = bundle.getParcelable(MOVIE_PARCELABLE_STRING);
        if (movie.getMovieName() != null)
            movie_title.setText(movie.getMovieName());
        if (movie.getMovieRating() != null) {
            String ratingText = movie.getMovieRating() + " / 10";
            rating.setText(ratingText);
        }
        overview.setText(movie.getMovieOverview());
        release_date.setText(formatDate(movie.getMovieReleaseDate()));
        Picasso.with(this).load(movie.getMovieImage()).placeholder(R.drawable.movie_icon).error(R.drawable.movie_icon).into(movieImage);
        movieId = movie.getId();
        /*Check for saved instance state to restore data*/
        if (savedInstanceState == null || !savedInstanceState.containsKey(TRAILER_LIST_PARCELABLE_STRING) || !savedInstanceState.containsKey(REVIEW_LIST_PARCELABLE_STRING)) {
            trailerArrayList = new ArrayList<>();
            reviewArrayList = new ArrayList<>();
            if (connectionUtil.isOnline()) {
                loadTrailersList(movie.getId());
            } else {
                if (mToast != null) {
                    mToast.cancel();
                }

                mToast = Toast.makeText(this, NO_INTERNET_MESSAGE, Toast.LENGTH_LONG);
                mToast.show();
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            trailerArrayList = savedInstanceState.getParcelableArrayList(TRAILER_LIST_PARCELABLE_STRING);
            reviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_LIST_PARCELABLE_STRING);
        }

        checkForFavorite();

    }

    private void checkForFavorite() {
        Cursor c = getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI, null, FavoritesContract.FavoriteEntry._ID + " = " + DatabaseUtils.sqlEscapeString(movie.getId()), null, null);
        Log.d(TAG, c.getCount() + "");
        try {
            if (c.getCount() > 0) {
                mFavoriteButton.setText(getResources().getString(R.string.marked_favorite));
            } else {
                mFavoriteButton.setText(getResources().getString(R.string.mark_favorite));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    private boolean returnFavoriteStatus() {
        Cursor c = getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI, null, FavoritesContract.FavoriteEntry._ID + " = " + DatabaseUtils.sqlEscapeString(movie.getId()), null, null);
        try {
            if (c.getCount() > 0) {
                c.close();
                return false;
            } else {
                c.close();
                return true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    /*Method to format date
    * Parameters: Date String to be formatted
    */
    private String formatDate(String inputDateString) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(inputDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputFormat.format(date);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        watchYoutubeVideo(trailerArrayList.get(clickedItemIndex).getKey());
    }

    /*Open Youtube Intent
    * Parameters: Youtube Video Id
    * */
    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    /* Method to provide sort option to be chosen
    Parameters: popular/top_rated */
    private void loadTrailersList(String movieId) {
        URL movieDbUrl = NetworkUtils.buildTrailerUrl(movieId);
        new GetTrailersListTask().execute(movieDbUrl);
    }

    /*AsyncTask to fetch the list of trailers
    * Parameters: URL, Void, String - Result String in JSON Format
    */
    private class GetTrailersListTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTrailersList.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieResults = null;
            try {
                movieResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieResults;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Log.d(TAG, s);
            mProgressBar.setVisibility(View.GONE);
            mTrailersList.setVisibility(View.VISIBLE);
            try {
                structureJSON(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadReviewsList(movieId);
        }
    }

    /* Method to provide sort option to be chosen
   Parameters: popular/top_rated */
    private void loadReviewsList(String movieId) {
        URL movieDbUrl = NetworkUtils.buildReviewUrl(movieId);
        new GetReviewsListTask().execute(movieDbUrl);
    }

    /*AsyncTask to fetch the list of reviews
    * Parameters: URL, Void, String - Result String in JSON Format
    */
    private class GetReviewsListTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mReviewsList.setVisibility(View.GONE);
            mReviewsProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieResults = null;
            try {
                movieResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieResults;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Log.d(TAG, s);
            mReviewsProgressBar.setVisibility(View.GONE);
            mReviewsList.setVisibility(View.VISIBLE);
            try {
                structureReviewsJSON(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Method to convert JSON string into movie objects
    * and populate them in recycler view
    * Parameters: JSON formatted String
    */
    private void structureJSON(String jsonString) throws JSONException {
        //movieArrayList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        //String resultCount = jsonObject.getString("total_results");
        JSONArray resultsArray = jsonObject.getJSONArray("results");
        int resultArrayLength = resultsArray.length();
        Log.d(TAG, "Size of json array " + resultArrayLength);
        for (int i = 0; i < resultArrayLength; i++) {
            JSONObject resultJSONObject = resultsArray.getJSONObject(i);
            String id = resultJSONObject.getString("id");
            String name = resultJSONObject.getString("name");
            String site = resultJSONObject.getString("site");
            String size = resultJSONObject.getString("size");
            String type = resultJSONObject.getString("type");
            String key = resultJSONObject.getString("key");
            Trailer trailer = new Trailer(id, name, site, size, type, key);
            trailerArrayList.add(trailer);
        }
        // Log.d(TAG, movieArrayList.size() + "");
        if (trailerAdapter == null) {
            trailerAdapter = new TrailerAdapter(resultArrayLength, trailerArrayList, MovieDetailActivity.this, this);
            mTrailersList.setAdapter(trailerAdapter);
        } else {
            trailerAdapter.notifyDataSetChanged();
        }

    }

    /*Method to convert JSON string into movie objects
    * and populate them in recycler view
    * Parameters: JSON formatted String
    */
    private void structureReviewsJSON(String jsonString) throws JSONException {
        //movieArrayList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        //String resultCount = jsonObject.getString("total_results");
        JSONArray resultsArray = jsonObject.getJSONArray("results");
        int resultArrayLength = resultsArray.length();
        Log.d(TAG, "Size of json array " + resultArrayLength);
        for (int i = 0; i < resultArrayLength; i++) {
            JSONObject resultJSONObject = resultsArray.getJSONObject(i);
            String id = resultJSONObject.getString("id");
            String author = resultJSONObject.getString("author");
            String content = resultJSONObject.getString("content");
            Review review = new Review(id, author, content);
            reviewArrayList.add(review);
        }
        // Log.d(TAG, movieArrayList.size() + "");
        if (reviewAdapter == null) {
            reviewAdapter = new ReviewAdapter(resultArrayLength, reviewArrayList, MovieDetailActivity.this);
            mReviewsList.setAdapter(reviewAdapter);
        } else {
            reviewAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button:
                if (returnFavoriteStatus()) {
                    insertData(movie);
                } else {
                    deleteData(movie);
                }
                checkForFavorite();
                break;
            case R.id.see_reviews_button:
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }
    }

    // insert data into database
    public void insertData(Movie movie) {
        ContentValues contentValues = new ContentValues();
        // Loop through static array of Flavors, add each to an instance of ContentValues
        // in the array of ContentValues

        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_IMAGE, movie.getMovieImage());
        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_NAME,
                movie.getMovieName());
        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_OVERVIEW,
                movie.getMovieOverview());
        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_RATING,
                movie.getMovieRating());
        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                movie.getMovieReleaseDate());
        contentValues.put(FavoritesContract.FavoriteEntry._ID,
                movie.getId());

        // bulkInsert our ContentValues array
        getContentResolver().insert(FavoritesContract.FavoriteEntry.CONTENT_URI, contentValues);
    }

    // delete data into database
    public void deleteData(Movie movie) {
        Log.d(TAG, "Already in the database");
        getContentResolver().delete(FavoritesContract.FavoriteEntry.CONTENT_URI, FavoritesContract.FavoriteEntry._ID + "=" + movie.getId(), null);
    }

}
