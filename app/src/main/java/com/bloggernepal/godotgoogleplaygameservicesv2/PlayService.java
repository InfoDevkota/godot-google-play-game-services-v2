package com.bloggernepal.godotgoogleplaygameservicesv2;

import android.util.Log;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

public class PlayService extends GodotPlugin {
    public PlayService(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotGooglePlayGameServicesV2";
    }

    @UsedByGodot
    public void firstTest() {
        Log.i("godot", "First Test Called");
    }
}
