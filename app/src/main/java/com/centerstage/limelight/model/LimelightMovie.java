package com.centerstage.limelight.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by Smitesh on 7/20/2015.
 * A Parcelable Movie.
 */
public class LimelightMovie implements Parcelable {

    private Integer id;
    private String movieTitle;
    private String tagline;
    private Date releaseDate;
    private List<Genre> genres;
    private Integer runtime;
    private Double userRating;
    private Integer numRatings;
    private String synopsis;
    private List<Language> languages;
    private Integer budget;
    private String backdropPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public Integer getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(Integer numRatings) {
        this.numRatings = numRatings;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public LimelightMovie() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.movieTitle);
        dest.writeString(this.tagline);
        dest.writeLong(releaseDate != null ? releaseDate.getTime() : -1);
        dest.writeTypedList(genres);
        dest.writeValue(this.runtime);
        dest.writeValue(this.userRating);
        dest.writeValue(this.numRatings);
        dest.writeString(this.synopsis);
        dest.writeTypedList(languages);
        dest.writeValue(this.budget);
        dest.writeString(this.backdropPath);
    }

    protected LimelightMovie(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.movieTitle = in.readString();
        this.tagline = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
        this.genres = in.createTypedArrayList(Genre.CREATOR);
        this.runtime = (Integer) in.readValue(Integer.class.getClassLoader());
        this.userRating = (Double) in.readValue(Double.class.getClassLoader());
        this.numRatings = (Integer) in.readValue(Integer.class.getClassLoader());
        this.synopsis = in.readString();
        this.languages = in.createTypedArrayList(Language.CREATOR);
        this.budget = (Integer) in.readValue(Integer.class.getClassLoader());
        this.backdropPath = in.readString();
    }

    public static final Creator<LimelightMovie> CREATOR = new Creator<LimelightMovie>() {
        public LimelightMovie createFromParcel(Parcel source) {
            return new LimelightMovie(source);
        }

        public LimelightMovie[] newArray(int size) {
            return new LimelightMovie[size];
        }
    };
}