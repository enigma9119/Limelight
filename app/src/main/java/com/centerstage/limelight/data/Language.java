package com.centerstage.limelight.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Smitesh on 7/20/2015.
 * A Parcelable Language.
 */
public class Language implements Parcelable {

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

    public Language() {
    }

    protected Language(Parcel in) {
        this.iso_639_1 = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        public Language createFromParcel(Parcel source) {
            return new Language(source);
        }

        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
}
