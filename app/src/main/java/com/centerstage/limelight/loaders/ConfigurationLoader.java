package com.centerstage.limelight.loaders;

import android.content.Context;

import com.centerstage.limelight.MainActivity;
import com.uwetrottmann.tmdb.entities.Configuration;

/**
 * Created by Smitesh on 8/22/2015.
 * Loader for tmdb configuration data.
 */
public class ConfigurationLoader extends DataLoader<Configuration> {

    public ConfigurationLoader(Context context) {
        super(context);
    }

    @Override
    public Configuration loadInBackground() {
        return MainActivity.sTmdbService.getConfiguration();
    }
}
