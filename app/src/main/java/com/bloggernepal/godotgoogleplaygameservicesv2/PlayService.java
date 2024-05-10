package com.bloggernepal.godotgoogleplaygameservicesv2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.HashSet;
import java.util.Set;

public class PlayService extends GodotPlugin {

    boolean authenticated = false;
    GamesSignInClient gamesSignInClient;


//    Signals
    static SignalInfo FIRST_TEST = new SignalInfo("on_first_test", Boolean.class);
    static SignalInfo SIGN_IN = new SignalInfo("on_sign_in", Boolean.class);


    public PlayService(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotGooglePlayGameServicesV2";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> pluginSignals = new HashSet<>();
        pluginSignals.add(FIRST_TEST);
        pluginSignals.add(SIGN_IN);
        return pluginSignals;
    }

    @UsedByGodot
    public void firstTest() {
        Log.i("godot", "First Test Called");
        emitSignal(FIRST_TEST.getName(), true);
    }

    @UsedByGodot
    public void initialize(){
        Log.i("godot", "Play Game SDK Initialized");
        PlayGamesSdk.initialize(getActivity());

        gamesSignInClient = PlayGames.getGamesSignInClient(getActivity());

        gamesSignInClient.isAuthenticated().addOnCompleteListener(isAuthenticatedTask -> {
            Log.i("godot", "Got Play Auto Login Result");
            authenticated =
                    (isAuthenticatedTask.isSuccessful() &&
                            isAuthenticatedTask.getResult().isAuthenticated());

//            notify godot as well
            emitSignal(SIGN_IN.getName(), authenticated);

            if (authenticated) {
                Log.i("godot", "User authenticated");
                // Continue with Play Games Services
            } else {
                Log.i("godot", "User not authenticated");
                // Disable your integration with Play Games Services or show a
                // login button to ask  players to sign-in. Clicking it should
                // call GamesSignInClient.signIn().
            }
        });

    }

    @UsedByGodot
    public boolean isAuthenticated() {
        return authenticated;
    }

}
