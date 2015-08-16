package com.centerstage.limelight.data;

import android.test.AndroidTestCase;

import com.centerstage.limelight.model.LimelightMovie;

/**
 * Created by Smitesh on 8/15/2015.
 * Utilities to make testing easier.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateMovie(LimelightMovie testMovie, LimelightMovie queriedMovie) {
        assertEquals(testMovie.getMovieId(), queriedMovie.getMovieId());
        assertEquals(testMovie.getMovieTitle(), queriedMovie.getMovieTitle());
        assertEquals(testMovie.getUserRating(), queriedMovie.getUserRating());

        // Test the Genre object
        assertNotNull(testMovie.getGenres().get(0));
        assertNotNull(queriedMovie.getGenres().get(0));
        assertEquals(testMovie.getGenres().get(0).getId(), queriedMovie.getGenres().get(0).getId());
        assertEquals(testMovie.getGenres().get(0).getName(), queriedMovie.getGenres().get(0).getName());
    }
}
