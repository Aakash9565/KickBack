package com.androidbeasts.kickback.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private final static String TAG = "NetworkUtils";

    private final static String MOVIE_DB_BASE_URL =
            "https://api.themoviedb.org/3/movie/";

    private final static String PARAM_QUERY = "api_key";

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
    public final static String SORT_BY_POPULARITY = "popular";
    public final static String SORT_BY_RATING = "top_rated";


    public static URL buildUrl(String sortBy) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon().appendPath(sortBy)
                .appendQueryParameter(PARAM_QUERY, Constants.MV_DB_API)
                .build();
        //Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?api_key=60e8433855c25f0ecb1236c0eb7a34ea");

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "URL is>>>>>>>>>> " + url);
        return url;
    }

    public static URL buildTrailerUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(PARAM_QUERY, Constants.MV_DB_API)
                .build();
        //Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?api_key=60e8433855c25f0ecb1236c0eb7a34ea");

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "URL is>>>>>>>>>> " + url);
        return url;
    }

    public static URL buildReviewUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(PARAM_QUERY, Constants.MV_DB_API)
                .build();
        //Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?api_key=60e8433855c25f0ecb1236c0eb7a34ea");

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "URL is>>>>>>>>>> " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
