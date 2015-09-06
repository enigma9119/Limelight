package com.centerstage.limelight.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import nl.littlerobots.cupboard.tools.convert.ListFieldConverterFactory;
import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
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
}
