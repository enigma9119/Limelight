package com.centerstage.limelight;

import android.net.ParseException;

import com.uwetrottmann.tmdb.Tmdb;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

/**
 * Created by Smitesh on 7/8/2015.
 * Used to make all the actual network requests to Tmdb.
 */
public class TmdbService {

    protected static final String API_KEY = "2edea78f94e1d52b5f85909d030187d2";

    private final Tmdb manager = new Tmdb();

    public TmdbService() {
        manager.setApiKey(API_KEY);
    }

    protected final Tmdb getManager() {
        return manager;
    }

    public Configuration getConfiguration() {
        return getManager().configurationService().configuration();
    }

    public MovieResultsPage getPopular(int pageNumber) {
        return getManager().moviesService().popular(null, null);
    }

    public MovieResultsPage getNowPlaying(int pageNumber) {
        return getManager().moviesService().nowPlaying(pageNumber, null);
    }

    public MovieResultsPage getUpcoming(int pageNumber) {
        return getManager().moviesService().upcoming(pageNumber, null);
    }

    public Movie getMovie(int tmdbId) {
        return getManager().moviesService().summary(tmdbId, null, null);
    }

    public void movieSearch(String searchQuery) throws ParseException {
        MovieResultsPage movieResults = getManager().searchService().movie(searchQuery,
                null, null, null, null, null, null);
    }
}
