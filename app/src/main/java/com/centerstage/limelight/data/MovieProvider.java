package com.centerstage.limelight.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.google.gson.Gson;

import nl.littlerobots.cupboard.tools.convert.ListFieldConverterFactory;
import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Smitesh on 8/16/2015.
 * An abstraction to provide movie data.
 */
public class MovieProvider extends CupboardContentProvider {

    public static final String CONTENT_AUTHORITY = "com.centerstage.limelight.data.provider";

    public static final String DATABASE_NAME = "limelight.db";
    private static final int DATABASE_VERSION = 1;

    private Uri mMovieUri;

    static {
        CupboardFactory.setCupboard(new CupboardBuilder().registerFieldConverterFactory(new ListFieldConverterFactory(new Gson())).build());
        cupboard().register(LimelightMovie.class);
    }

    public MovieProvider() {
        super(CONTENT_AUTHORITY, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public SQLiteOpenHelper getDatabaseHelper(Context context) {
        return super.getDatabaseHelper(context);
    }

    @Override
    public boolean onCreate() {
        UriHelper uriHelper = UriHelper.with(MovieProvider.CONTENT_AUTHORITY);
        mMovieUri = uriHelper.getUri(LimelightMovie.class);
        return true;
    }

    // Insert a movie into the database
    public Uri insertMovie(LimelightMovie movie) {
        ContentValues movieValues = cupboard().withEntity(LimelightMovie.class).toContentValues(movie);

        return insertInTransaction(mMovieUri, movieValues);
    }

    // Get a Cursor containing all the movies in the database
    public Cursor queryMovies() {
        return query(mMovieUri, null, null, null, null);
    }

    // Get a single movie from the database using movie id
    public LimelightMovie getMovieById(long id) {
        return cupboard().withContext(getContext()).get(ContentUris.withAppendedId(mMovieUri, id), LimelightMovie.class);
    }
}
