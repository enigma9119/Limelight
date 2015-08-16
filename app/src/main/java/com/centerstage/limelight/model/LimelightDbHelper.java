package com.centerstage.limelight.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import nl.littlerobots.cupboard.tools.convert.ListFieldConverterFactory;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Smitesh on 8/15/2015.
 * Database for Limelight.
 */
public class LimelightDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "limelight.db";
    private static final int DATABASE_VERSION = 1;

    static {
        CupboardFactory.setCupboard(new CupboardBuilder().registerFieldConverterFactory(new ListFieldConverterFactory(new Gson())).build());
        cupboard().register(LimelightMovie.class);
    }

    public LimelightDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted
        cupboard().withDatabase(db).upgradeTables();
    }
}
