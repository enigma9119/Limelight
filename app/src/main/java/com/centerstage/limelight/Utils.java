package com.centerstage.limelight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.ImageView;

import com.centerstage.limelight.data.LimelightMovie;
import com.centerstage.limelight.data.ParcelableGenre;
import com.centerstage.limelight.data.ParcelableLanguage;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.SpokenLanguage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Smitesh on 7/8/2015.
 * General Utilities.
 */
public class Utils {

    // Converts text to a bitmap drawable that can placed in an ImageView
    public static BitmapDrawable textAsBitmapDrawable(Context context, String text, float textSize, int textColor, int imageWidth, int imageHeight) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setFakeBoldText(true);

        Bitmap image;
        if (imageWidth > 0 && imageHeight > 0) {
            image = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        } else {
            return null;
        }

        Canvas canvas = new Canvas(image);
        StaticLayout textLayout = new StaticLayout(text, paint, imageWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.save();
        canvas.translate(0, imageHeight/2.5f);
        textLayout.draw(canvas);
        canvas.restore();

        return new BitmapDrawable(context.getResources(), image);
    }

    // Checks whether an internet connection is available
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // Convert from movie object to parcelable Limelight movie object
    public static LimelightMovie convertMovieToLimelightMovie(Movie movie, Configuration config) {
        LimelightMovie limelightMovie = new LimelightMovie();
        limelightMovie.setMovieId(movie.id);
        limelightMovie.setMovieTitle(movie.original_title);
        limelightMovie.setTagline(movie.tagline);
        limelightMovie.setReleaseDate(movie.release_date);

        if (movie.genres != null) {
            ArrayList<ParcelableGenre> parcelableGenres = new ArrayList<>(movie.genres.size());

            for (Genre genre : movie.genres) {
                ParcelableGenre parcelableGenre = new ParcelableGenre();
                parcelableGenre.setId(genre.id);
                parcelableGenre.setName(genre.name);

                parcelableGenres.add(parcelableGenre);
            }

            limelightMovie.setGenres(parcelableGenres);
        }

        limelightMovie.setRuntime(movie.runtime);
        limelightMovie.setUserRating(movie.vote_average);
        limelightMovie.setNumRatings(movie.vote_count);
        limelightMovie.setSynopsis(movie.overview);

        if (movie.spoken_languages != null) {
            ArrayList<ParcelableLanguage> parcelableLanguages = new ArrayList<>(movie.spoken_languages.size());

            for (SpokenLanguage spokenLanguage : movie.spoken_languages) {
                ParcelableLanguage parcelableLanguage = new ParcelableLanguage();
                parcelableLanguage.setIso_639_1(spokenLanguage.iso_639_1);
                parcelableLanguage.setName(spokenLanguage.name);

                parcelableLanguages.add(parcelableLanguage);
            }

            limelightMovie.setLanguages(parcelableLanguages);
        }

        limelightMovie.setBudget(movie.budget);

        // Purpose of configuration data is to load common elements that don't change frequently
        // separately to keep the actual API responses as light as possible
        String completePosterPath = config.images.base_url + config.images.poster_sizes.get(3) + movie.poster_path;
        limelightMovie.setPosterPath(completePosterPath);

        String completeBackdropPath = config.images.base_url + config.images.backdrop_sizes.get(1) + movie.backdrop_path;
        limelightMovie.setBackdropPath(completeBackdropPath);

        return limelightMovie;
    }

    // Convert ImageView to a byte array
    public static byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
