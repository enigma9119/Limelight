package com.centerstage.limelight.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Smitesh on 8/29/2015.
 * A Parcelable Review.
 */
public class ParcelableReview implements Parcelable {

    Long _id;
    String reviewId;
    String author;
    String content;
    String url;

    private Long getId() {
        return _id;
    }

    private void setId(Long _id) {
        this._id = _id;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this._id);
        dest.writeString(this.reviewId);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    public ParcelableReview() {
    }

    protected ParcelableReview(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.reviewId = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ParcelableReview> CREATOR = new Creator<ParcelableReview>() {
        public ParcelableReview createFromParcel(Parcel source) {
            return new ParcelableReview(source);
        }

        public ParcelableReview[] newArray(int size) {
            return new ParcelableReview[size];
        }
    };
}
