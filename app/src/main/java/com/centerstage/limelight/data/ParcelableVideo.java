package com.centerstage.limelight.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Smitesh on 9/7/2015.
 * A Parcelable Video.
 */
public class ParcelableVideo implements Parcelable {

    String name;
    String url;
    String type;
    String site;
    String id;
    String iso_639_1;
    Integer size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeString(this.site);
        dest.writeString(this.id);
        dest.writeString(this.iso_639_1);
        dest.writeValue(this.size);
    }

    public ParcelableVideo() {
    }

    protected ParcelableVideo(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.type = in.readString();
        this.site = in.readString();
        this.id = in.readString();
        this.iso_639_1 = in.readString();
        this.size = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ParcelableVideo> CREATOR = new Creator<ParcelableVideo>() {
        public ParcelableVideo createFromParcel(Parcel source) {
            return new ParcelableVideo(source);
        }

        public ParcelableVideo[] newArray(int size) {
            return new ParcelableVideo[size];
        }
    };
}
