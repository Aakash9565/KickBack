package com.androidbeasts.kickback.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*DBHelper class for managing database*/
public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MovieDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoriteEntry.TABLE_FAVORITES + "(" + FavoritesContract.FavoriteEntry._ID +
                " TEXT NOT NULL, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FavoritesContract.FavoriteEntry.COLUMN_IMAGE +
                " TEXT NOT NULL, " + FavoritesContract.FavoriteEntry.COLUMN_RATING +
                " TEXT NOT NULL, " + FavoritesContract.FavoriteEntry.COLUMN_OVERVIEW +
                " TEXT NOT NULL, " +
                FavoritesContract.FavoriteEntry.COLUMN_RELEASE_DATE +
                " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoriteEntry.TABLE_FAVORITES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                FavoritesContract.FavoriteEntry.TABLE_FAVORITES + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
