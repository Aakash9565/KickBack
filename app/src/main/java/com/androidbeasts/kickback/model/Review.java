package com.androidbeasts.kickback.model;

import android.os.Parcel;
import android.os.Parcelable;

/*Model class for review*/
public class Review implements Parcelable{

    private String id;
    private String author;
    private String content;

    public Review(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    protected Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

//{"id":211672,"page":1,"results":[{"id":"55a58e46c3a3682bb2000065","author":"Andres Gomez","content":"The minions are a nice idea and the animation and London recreation is really good, but that's about it.\r\n\r\nThe script is boring and the jokes not really funny.","url":"https://www.themoviedb.org/review/55a58e46c3a3682bb2000065"},{"id":"55e108c89251416c0b0006dd","author":"movizonline.com","content":"a nice idea and the animation.the new thing in animation field.a movie that every one should like an kid or old man.","url":"https://www.themoviedb.org/review/55e108c89251416c0b0006dd"}],"total_pages":1,"total_results":2}

