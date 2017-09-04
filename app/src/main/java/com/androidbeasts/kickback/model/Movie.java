package com.androidbeasts.kickback.model;

import android.os.Parcel;
import android.os.Parcelable;

/*Model class for movie*/
public class Movie implements Parcelable {

    private String id;
    private String movieName;
    private String movieImage;
    private String movieRating;
    private String movieOverview;
    private String movieReleaseDate;

    public Movie(String id, String movie_name, String movie_image, String movie_rating, String movie_overview, String movie_release_date) {
        this.id = id;
        this.movieName = movie_name;
        this.movieImage = movie_image;
        this.movieRating = movie_rating;
        this.movieOverview = movie_overview;
        this.movieReleaseDate = movie_release_date;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        movieName = in.readString();
        movieImage = in.readString();
        movieRating = in.readString();
        movieOverview = in.readString();
        movieReleaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(movieName);
        dest.writeString(movieImage);
        dest.writeString(movieRating);
        dest.writeString(movieOverview);
        dest.writeString(movieReleaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

}
