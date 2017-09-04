package com.androidbeasts.kickback.model;

import android.os.Parcel;
import android.os.Parcelable;

/*Model class for trailer*/
public class Trailer implements Parcelable {

    private String id;
    private String trailerName;
    private String trailerSite;
    private String trailerSize;
    private String videoType;
    private String key;

    public Trailer(String id, String trailerName, String trailerSite, String trailerSize, String videoType, String key) {
        this.id = id;
        this.trailerName = trailerName;
        this.trailerSite = trailerSite;
        this.trailerSize = trailerSize;
        this.videoType = videoType;
        this.key = key;
    }

    protected Trailer(Parcel in) {
        id = in.readString();
        trailerName = in.readString();
        trailerSite = in.readString();
        trailerSize = in.readString();
        videoType = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(trailerName);
        dest.writeString(trailerSite);
        dest.writeString(trailerSize);
        dest.writeString(videoType);
        dest.writeString(key);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrailerSite() {
        return trailerSite;
    }

    public void setTrailerSite(String trailerSite) {
        this.trailerSite = trailerSite;
    }

    public String getTrailerSize() {
        return trailerSize;
    }

    public void setTrailerSize(String trailerSize) {
        this.trailerSize = trailerSize;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }
}
