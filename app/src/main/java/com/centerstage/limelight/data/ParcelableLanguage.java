package com.centerstage.limelight.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Smitesh on 7/20/2015.
 * A Parcelable Language.
 */
public class ParcelableLanguage implements Parcelable {

    String iso_639_1;
    String name;

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iso_639_1);
        dest.writeString(this.name);
    }

    public ParcelableLanguage() {
    }

    protected ParcelableLanguage(Parcel in) {
        this.iso_639_1 = in.readString();
        this.name = in.readString();
    }

    public static final Creator<ParcelableLanguage> CREATOR = new Creator<ParcelableLanguage>() {
        public ParcelableLanguage createFromParcel(Parcel source) {
            return new ParcelableLanguage(source);
        }

        public ParcelableLanguage[] newArray(int size) {
            return new ParcelableLanguage[size];
        }
    };
}
