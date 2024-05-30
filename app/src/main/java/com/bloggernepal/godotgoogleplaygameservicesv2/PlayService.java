package com.bloggernepal.godotgoogleplaygameservicesv2;

import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_WEEKLY;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bloggernepal.godotgoogleplaygameservicesv2.models.PlayerProfile;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AuthenticationResult;
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class PlayService extends GodotPlugin {

    final String TAG = "godot GPGSv2";

    boolean authenticated = false;
    GamesSignInClient gamesSignInClient;
    //    Signals
    static SignalInfo FIRST_TEST = new SignalInfo("on_first_test", Boolean.class);
    static SignalInfo SIGN_IN = new SignalInfo("on_sign_in", Boolean.class);
    static SignalInfo PLAYER_INFO = new SignalInfo("on_player_info", String.class);
    static SignalInfo MANUAL_SIGN_IN = new SignalInfo("on_manual_sign_in", Boolean.class);
    static SignalInfo OAUTH_AUTH_CODE = new SignalInfo("on_oauth_auth_code", Boolean.class, String.class);

    //    I would like to have just a single call back with success and fail status
//     let's ignore these if when we implement those Immediate ones we will have these callbacks
//    static SignalInfo ACHIEVEMENT_SET_STEP = new SignalInfo("on_achievement_step_set", Boolean.class);
//    static SignalInfo ACHIEVEMENT_REVELED = new SignalInfo("on_achievement_reveled", Boolean.class);
//    static SignalInfo ACHIEVEMENT_UNLOCKED = new SignalInfo("on_achievement_unlocked", Boolean.class);
    static SignalInfo SHOW_ACHIEVEMENT = new SignalInfo("on_achievement_shown", Boolean.class);

    static SignalInfo GET_LEADERBOARD_SCORE = new SignalInfo("on_leaderboard_score", Boolean.class, Integer.class);
    static SignalInfo SHOW_LEADERBOARD = new SignalInfo("on_leaderboard_shown", Boolean.class);

    // for FCM
    static SignalInfo NOTIFICATION_PERMISSION = new SignalInfo("on_notification_permission", Boolean.class);
    static SignalInfo FCM_TOKEN = new SignalInfo("on_fcm_token", Boolean.class, String.class);
    static SignalInfo NEW_FCM_TOKEN = new SignalInfo("on_new_fcm_token", Boolean.class, String.class);


    // end for FCM


    static int RC_ACHIEVEMENT_UI = 9003;

    public PlayService(Godot godot) {
        super(godot);
        Helper.getInstance().setPlayService(this);
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
//        pluginSignals.add(ACHIEVEMENT_SET_STEP);
//        pluginSignals.add(ACHIEVEMENT_REVELED);
//        pluginSignals.add(ACHIEVEMENT_UNLOCKED);
        pluginSignals.add(SHOW_ACHIEVEMENT);
        pluginSignals.add(GET_LEADERBOARD_SCORE);
        pluginSignals.add(SHOW_LEADERBOARD);


        // fcm
        pluginSignals.add(NOTIFICATION_PERMISSION);
        pluginSignals.add(FCM_TOKEN);
        pluginSignals.add(NEW_FCM_TOKEN);
        // fcm end
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
                .addOnCompleteListener(task -> {
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

    @UsedByGodot
    public void achievement_set_step(String achievementID, int step) {
        AchievementsClient achievementsClient = PlayGames.getAchievementsClient(getActivity());

        // This is the fire-and-forget form of the API.
        // https://developers.google.com/android/reference/com/google/android/gms/games/AchievementsClient#setSteps(java.lang.String,%20int)
        achievementsClient.setSteps(achievementID, step);
    }

    @UsedByGodot
    public void achievement_revele(String achievementID) {
        AchievementsClient achievementsClient = PlayGames.getAchievementsClient(getActivity());

        achievementsClient.reveal(achievementID);
    }

    @UsedByGodot
    public void achievement_unlock(String achievementID) {
        AchievementsClient achievementsClient = PlayGames.getAchievementsClient(getActivity());

        achievementsClient.unlock(achievementID);
        // for this unlock it would be better to use that unlockImmediate, thus it will
        // notify the client and show the achievement unlocked
        // for easy let it be
    }

    @UsedByGodot
    public void show_achievement() {
        AchievementsClient achievementsClient = PlayGames.getAchievementsClient(getActivity());

        achievementsClient.getAchievementsIntent().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Intent achievementIntent = task.getResult();

                        getActivity().startActivityForResult(achievementIntent, RC_ACHIEVEMENT_UI);
                        emitSignal(SHOW_ACHIEVEMENT.getName(), true);
                    }
                }
        );
        // for this unlock it would be better to use that unlockImmediate, thus it will
        // notify the client and show the achievement unlocked
        // for easy let it be
    }

    @UsedByGodot
    public void send_event(String eventId, int amount) {
        Log.i("godot", "send_event called");
        EventsClient eventsClient = PlayGames.getEventsClient(getActivity());

//        This is the fire-and-forget API
//        https://developers.google.com/android/reference/com/google/android/gms/games/EventsClient#public-abstract-void-increment-string-eventid,-int-incrementamount
        eventsClient.increment(eventId, amount);
    }

    @UsedByGodot
    public void show_leaderboard(String leaderboardId) {
        LeaderboardsClient leaderboardsClient = PlayGames.getLeaderboardsClient(getActivity());

        leaderboardsClient.getLeaderboardIntent(leaderboardId).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Intent leaderboardIntent = task.getResult();

                        getActivity().startActivityForResult(leaderboardIntent, RC_ACHIEVEMENT_UI);
                        emitSignal(SHOW_LEADERBOARD.getName(), true);
                    }
                }
        );
    }

    @UsedByGodot
    public void get_leaderboard_score(String leaderboard, int span, int collection) {
//        Here the span and collection should be some form of enum in godot side.

        LeaderboardsClient leaderboardsClient = PlayGames.getLeaderboardsClient(getActivity());

//        int span_can_be = LeaderboardVariant.TIME_SPAN_DAILY; //0
//        int span_can_be = LeaderboardVariant.TIME_SPAN_WEEKLY; //1
//        int span_can_be = LeaderboardVariant.TIME_SPAN_ALL_TIME; //2

//        int collection_can_be =  LeaderboardVariant.COLLECTION_PUBLIC; //0
//        int collection_can_be =  LeaderboardVariant.COLLECTION_FRIENDS; //2

        leaderboardsClient.loadCurrentPlayerLeaderboardScore(leaderboard, span, collection)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LeaderboardScore leaderboardScore = task.getResult().get();

                        // this looks more like for my personal use and less like a library
                        // if all goes good, we can implement necessary for now
                        // let's just return the raw score

                        if (leaderboardScore != null) {
                            emitSignal(GET_LEADERBOARD_SCORE.getName(), true, (int) leaderboardScore.getRawScore());
                        }
                        {
                            // assuming there is no score for this time frame and collection thus send 0
                            // and make it as success as well
                            emitSignal(GET_LEADERBOARD_SCORE.getName(), true, 0);
                        }

                    }
                });
    }

    @UsedByGodot
    public void submit_score_to_leaderboard(String leaderboardId, int value) {
        LeaderboardsClient leaderboardsClient = PlayGames.getLeaderboardsClient(getActivity());

        leaderboardsClient.submitScore(leaderboardId, value);
    }


////////////////////////////////////////////////////////////////////////////////////
    // Contents below this is for FCM
///////////////////////////////////////////////////////////////////////////////////
    private final int PERMISSION_REQUEST_CODE = 1001;

    @UsedByGodot
    public void initializeFirebase(String applicationId, String apiKey, String projectId) {
        Log.e(TAG, "Firebaseapp initialication called");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(applicationId)
                .setApiKey(apiKey)
                .setProjectId(projectId)
                .build();

        FirebaseApp.initializeApp(getActivity(), options);
    }

    @UsedByGodot
    public void askNotificationPermission() {
        // Access the activity from GodotPlugin
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
                // FCM SDK (and your app) can post notifications.
                emitSignal(NOTIFICATION_PERMISSION.getName(), true);
            } else {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onMainRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onMainRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // FCM SDK (and your app) can post notifications.
                emitSignal(NOTIFICATION_PERMISSION.getName(), true);

            } else {
                // Permission denied
                // Inform user that your app will not show notifications.
                emitSignal(NOTIFICATION_PERMISSION.getName(), false);
            }
        }
    }

    @UsedByGodot
    boolean has_notification_permission() {

        // To check if we have notification permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        }
            return false;
    }

    @UsedByGodot
    void get_fcm_token() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            emitSignal(FCM_TOKEN.getName(), false, "");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        emitSignal(FCM_TOKEN.getName(), true, token);
                    }
                });
    }

    protected void new_fcm_token_generated(String token) {
        emitSignal(NEW_FCM_TOKEN.getName(), true, token);
    }

}
