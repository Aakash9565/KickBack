package com.androidbeasts.kickback;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidbeasts.kickback.adapter.FavoritesAdapter;
import com.androidbeasts.kickback.data.FavoritesContract;
import com.androidbeasts.kickback.model.Movie;
import java.util.ArrayList;

/*This Activity shows the list of favorite movies*/
public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, FavoritesAdapter.ListItemClickListener {

    private final String TAG = this.getClass().getSimpleName();
    /*
     * References to RecyclerView and Adapter to reset the list to its
     * "pretty" state when the reset menu item is clicked.
     */
    private FavoritesAdapter mAdapter;
    private RecyclerView mMoviesList;
    private ArrayList<Movie> movieArrayList;
    private static final int CURSOR_LOADER_ID = 0;

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieArrayList != null) {
            outState.putParcelableArrayList(MOVIE_LIST_PARCELABLE_STRING, movieArrayList);
        }
        super.onSaveInstanceState(outState);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAdapter = new FavoritesAdapter(FavoritesActivity.this, null, this);
        mMoviesList = (RecyclerView) findViewById(R.id.movieListItems);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);
        /*if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST_PARCELABLE_STRING)) {
            movieArrayList = new ArrayList<>();
            loadMoviesList(NetworkUtils.SORT_BY_POPULARITY);
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_STRING);
        }*/

        Cursor c =
                getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        try {
            if (c.getCount() == 0) {
                Toast.makeText(getBaseContext(), "No Favorites Added Yet", Toast.LENGTH_LONG).show();
            } else {

                c.moveToFirst();
                movieArrayList = new ArrayList<>();

                while (!c.isAfterLast()) {
                    final String id = c.getString(c.getColumnIndex(FavoritesContract.FavoriteEntry._ID));
                    final String image = c.getString(c.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_IMAGE));
                    final String overview = c.getString(c.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_OVERVIEW));
                    final String release_date = c.getString(c.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_RELEASE_DATE));
                    final String name = c.getString(c.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_NAME));
                    final String rating = c.getString(c.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_RATING));
                    Movie movie = new Movie(id, name, image, rating, overview, release_date);
                    movieArrayList.add(movie);
                    c.moveToNext();
                }
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }finally {
            c.close();
        }

        // initialize loader
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie movie = movieArrayList.get(clickedItemIndex);
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(FavoritesActivity.this,
                FavoritesContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mAdapter.swapCursor(data);
            mMoviesList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        mMoviesList.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        mAdapter.swapCursor(getAllFavorites());
        mAdapter.notifyDataSetChanged();
    }

    private Cursor getAllFavorites() {
        return getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }
}
