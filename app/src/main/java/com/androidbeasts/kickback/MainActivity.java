package com.androidbeasts.kickback;

//

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.androidbeasts.kickback.adapter.MovieAdapter;
import com.androidbeasts.kickback.model.Movie;
import com.androidbeasts.kickback.utils.ConnectionUtil;
import com.androidbeasts.kickback.utils.Constants;
import com.androidbeasts.kickback.utils.NetworkUtils;
import com.androidbeasts.kickback.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/*This activity shows the list of movies based on settings of sorting by either most popular or most rated*/
public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private final String MOVIE_LIST_PARCELABLE_STRING = "movies";
    private final String TAG = this.getClass().getSimpleName();
    /*
     * References to RecyclerView and Adapter to reset the list to its
     * "pretty" state when the reset menu item is clicked.
     */
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private ArrayList<Movie> movieArrayList;
    private ConnectionUtil connectionUtil;
    private CoordinatorLayout mainLayout;
    private SharedPrefUtil shared;
    private AlertDialog dlg = null;
    private ProgressBar mProgressBar;
    private String NO_INTERNET_MESSAGE;

    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    //private Toast mToast;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieArrayList != null) {
            outState.putParcelableArrayList(MOVIE_LIST_PARCELABLE_STRING, movieArrayList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMoviesList = (RecyclerView) findViewById(R.id.movieListItems);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);

        connectionUtil = new ConnectionUtil(this);
        shared = new SharedPrefUtil(this);
        NO_INTERNET_MESSAGE = getResources().getString(R.string.no_internet_message);
        /*Check for saved instance state to restore data*/
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST_PARCELABLE_STRING)) {
            movieArrayList = new ArrayList<>();
            if (connectionUtil.isOnline()) {
                if (!shared.checkForKey("sort") || shared.getString("sort").equals("popular")) {
                    loadMoviesList(NetworkUtils.SORT_BY_POPULARITY);
                } else {
                    loadMoviesList(NetworkUtils.SORT_BY_RATING);
                }
            } else {
                Snackbar.make(mainLayout, NO_INTERNET_MESSAGE, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_STRING);
        }


        //Show sorting options on floating action button click
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }

    /* Method to provide sort option to be chosen
    Parameters: popular/top_rated */
    private void loadMoviesList(String sortBy) {
        URL movieDbUrl = NetworkUtils.buildUrl(sortBy);
        new GetMovieListTask().execute(movieDbUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
       /* if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();*/

        Movie movie = movieArrayList.get(clickedItemIndex);
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /*AsyncTask to fetch the list of movies
    * Parameters: URL, Void, String - Result String in JSON Format
    */
    private class GetMovieListTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMoviesList.setVisibility(View.GONE);
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
            mMoviesList.setVisibility(View.VISIBLE);
            try {
                structureJSON(s);
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
            String rating = resultJSONObject.getString("vote_average");
            String title = resultJSONObject.getString("title");
            String image = Constants.IMAGE_BASE_URL + resultJSONObject.getString("poster_path");
            String overview = resultJSONObject.getString("overview");
            String release_date = resultJSONObject.getString("release_date");
            Movie movie = new Movie(id, title, image, rating, overview, release_date);
            movieArrayList.add(movie);
        }
        // Log.d(TAG, movieArrayList.size() + "");
        if (mAdapter == null) {
            mAdapter = new MovieAdapter(resultArrayLength, movieArrayList, MainActivity.this, this);
            mMoviesList.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    /*Method to show dialog with sort options*/
    private void showOptionDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = inflater.inflate(R.layout.sort_by_dialog_layout, null);
        alertDialog.setView(convertView);
        //alertDialog.setTitle(title);
        RadioButton sort_by_popular = (RadioButton) convertView.findViewById(R.id.sort_by_popular);
        RadioButton sort_by_rating = (RadioButton) convertView.findViewById(R.id.sort_by_rating);
        if (shared.getString("sort").equals("popular") || !shared.checkForKey("sort")) {
            sort_by_popular.setChecked(true);
        } else {
            sort_by_rating.setChecked(true);
        }
        sort_by_popular.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dlg.dismiss();
                    if (movieArrayList != null) {
                        movieArrayList.clear();
                    }
                    shared.addString("sort", "popular");
                    if (connectionUtil.isOnline()) {
                        loadMoviesList(NetworkUtils.SORT_BY_POPULARITY);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Snackbar.make(mainLayout, NO_INTERNET_MESSAGE, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }

                }
            }
        });

        sort_by_rating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dlg.dismiss();
                    if (movieArrayList != null) {
                        movieArrayList.clear();
                    }
                    shared.addString("sort", "rating");
                    if (connectionUtil.isOnline()) {
                        loadMoviesList(NetworkUtils.SORT_BY_RATING);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Snackbar.make(mainLayout, NO_INTERNET_MESSAGE, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });

        dlg = alertDialog.show();

    }
}
