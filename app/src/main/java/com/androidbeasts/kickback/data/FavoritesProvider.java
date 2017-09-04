package com.androidbeasts.kickback.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/*Provider class for list of favorites extending content provider*/
public class FavoritesProvider extends ContentProvider {

    private static final String LOG_TAG = FavoritesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int FAVORITE = 100;
    private static final int FAVORITE_WITH_ID = 200;

    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, FavoritesContract.FavoriteEntry.TABLE_FAVORITES, FAVORITE);
        matcher.addURI(authority, FavoritesContract.FavoriteEntry.TABLE_FAVORITES + "/#", FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE: {
                return FavoritesContract.FavoriteEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITE_WITH_ID: {
                return FavoritesContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // All Favorites selected
            case FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoriteEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual Favorite based on Id selected
            case FAVORITE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoriteEntry.TABLE_FAVORITES,
                        projection,
                        FavoritesContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                long _id = db.insert(FavoritesContract.FavoriteEntry.TABLE_FAVORITES, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = FavoritesContract.FavoriteEntry.buildFavoritesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        try {
            getContext().getContentResolver().notifyChange(uri, null);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case FAVORITE:
                numDeleted = db.delete(
                        FavoritesContract.FavoriteEntry.TABLE_FAVORITES, selection, selectionArgs);
                // reset _ID
                /*db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritesContract.FavoriteEntry.TABLE_FAVORITES + "'");*/
                break;
            case FAVORITE_WITH_ID:
                numDeleted = db.delete(FavoritesContract.FavoriteEntry.TABLE_FAVORITES,
                        FavoritesContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
               /* db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritesContract.FavoriteEntry.TABLE_FAVORITES + "'");*/

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                numUpdated = db.update(FavoritesContract.FavoriteEntry.TABLE_FAVORITES,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID: {
                numUpdated = db.update(FavoritesContract.FavoriteEntry.TABLE_FAVORITES,
                        contentValues,
                        FavoritesContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        return numUpdated;
    }

}
