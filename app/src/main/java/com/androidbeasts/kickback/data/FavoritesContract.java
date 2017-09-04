package com.androidbeasts.kickback.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/*Contract class for favorites*/
public class FavoritesContract {

    public static final String CONTENT_AUTHORITY = "com.androidbeasts.kickback.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteEntry implements BaseColumns {
        // table name
        public static final String TABLE_FAVORITES = "favorites";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";


        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FAVORITES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;

        // for building URIs on insertion
        public static Uri buildFavoritesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
