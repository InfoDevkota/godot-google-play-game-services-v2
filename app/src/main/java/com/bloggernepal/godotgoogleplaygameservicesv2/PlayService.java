package com.bloggernepal.godotgoogleplaygameservicesv2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bloggernepal.godotgoogleplaygameservicesv2.models.PlayerProfile;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AuthenticationResult;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.Player;
import com.google.gson.Gson;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class PlayService extends GodotPlugin {

    boolean authenticated = false;
    GamesSignInClient gamesSignInClient;
    //    Signals
    static SignalInfo FIRST_TEST = new SignalInfo("on_first_test", Boolean.class);
    static SignalInfo SIGN_IN = new SignalInfo("on_sign_in", Boolean.class);
    static SignalInfo PLAYER_INFO = new SignalInfo("on_player_info", String.class);
    static SignalInfo MANUAL_SIGN_IN = new SignalInfo("on_manual_sign_in", Boolean.class);
    static SignalInfo OAUTH_AUTH_CODE = new SignalInfo("on_oauth_auth_code", Boolean.class, String.class);


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
        pluginSignals.add(PLAYER_INFO);
        pluginSignals.add(MANUAL_SIGN_IN);
        pluginSignals.add(OAUTH_AUTH_CODE);
        return pluginSignals;
    }

    @UsedByGodot
    public void firstTest() {
        Log.i("godot", "First Test Called");
        emitSignal(FIRST_TEST.getName(), true);
    }

    @UsedByGodot
    public void get_profile() {
        PlayGames.getPlayersClient(getActivity()).getCurrentPlayer().addOnCompleteListener(mTask -> {
                    Log.i("godot", "got player");

                    Player player = mTask.getResult();
                    // There are lots of data that we can get, but for simplicity just getting these
                    PlayerProfile playerProfile = new PlayerProfile(
                            player.getPlayerId(),
                            player.getDisplayName(),
                            player.getTitle()
                    );
                    emitSignal(PLAYER_INFO.getName(), new Gson().toJson(playerProfile));
                }
        );
    }


    @UsedByGodot
    public void initialize() {
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

                get_profile();

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

    @UsedByGodot
    public void manualSignIn() {
        Log.i("godot", "manaul signup received");
        // if not autosign is not success have a button that will allow user to sing in manually
        gamesSignInClient.signIn().addOnCompleteListener(mTask -> {
            Log.i("godot", "manaul signup response received");
            AuthenticationResult authenticationResult = mTask.getResult();
            authenticated = authenticationResult.isAuthenticated();

            emitSignal(MANUAL_SIGN_IN.getName(), authenticated);
//            Godot can use getProfile to get the profile details
        });
    }

    @UsedByGodot
    public void requestOAuthAuthCode(String OAuth2WebClient) {
        GamesSignInClient gamesSignInClient = PlayGames.getGamesSignInClient(getActivity());
        gamesSignInClient
                .requestServerSideAccess(OAuth2WebClient, /* forceRefreshToken= */ false)
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        String serverAuthToken = task.getResult();
                        emitSignal(OAUTH_AUTH_CODE.getName(), true, serverAuthToken);
                        // Send authentication code to the backend game server to be
                        // exchanged for an access token and used to verify the player
                        // via the Play Games Services REST APIs.
                    } else {
                        emitSignal(OAUTH_AUTH_CODE.getName(), false, "");
                        // Failed to retrieve authentication code.
                    }
                });

    }

}
