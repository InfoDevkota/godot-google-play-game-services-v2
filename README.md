# Godot Google Play Game Services V2

This Plugin is developed for one of our game [Carrom Karrom: Carrom Board](https://play.google.com/store/apps/details?id=com.bloggernepal.carrom), you can check the game for live demo. Our requirements may not be enough for your need but, you can modify to fit your need check [DEVELOPMENT.md](DEVELOPMENT.md)

With this plugin you can

Add Play Games services to your game to get frictionless zero-click sign-in

## How to use the Library

Here is a walk through video [Google Play Game Service v2 for Godot, Frictionless zero-click sign-in](https://youtu.be/y0IiEC3BHQw).

### Setup Play Game Service

1. First follow this to [Set up Play Game Service](https://developers.google.com/games/services/console/enabling)
    - Here you have to go to Play Console. If you have not already, need to make one time 25$ payment to get access to Play Console.
    - Create Google Cloud project
    - Choose external
    - Configure OAuth consent Screen
    - In Scope choose **auth/games_lite** and **auth/drive.appdata** only
    - Add SHA1 hash of your keystore, add debug, add production + from Google as well, as most of the signing key is handled by google, the published app is signed by different key than your production one (managed by google).
    - Let it be on testing phase
    - To release you have to have privacy policy and homepage for the game and both of them should be on a verified domain from `Search Console` and it could took few weeks. But generally on few days. (took 3 days for me)

The above details one are just for quick headstart, just read and keep in mind, but follow the above link throughly.
> **⚠ NOTE: Just choose those two scopes.**  
> For Play Game Service to auto login, you should choose **auth/games_lite** and **auth/drive.appdata** only.

You should have the game_services_project_id by now, you can get it in the play console.

### Setup Project
1. If you have not already, install the android build tool
2. Download the `
GodotGooglePlayGameServicesV2-vx.x.x` zip from [release](https://github.com/InfoDevkota/godot-google-play-game-services-v2/releases)
3. extract it and place the `GodotGooglePlayGameServicesV2-vx.x.x.aar` and `GodotGooglePlayGameServicesV2.gdap` in `android/plugins`
4. Modify `android/build/AndroidManifest.xml` and add 
    ```
    <meta-data android:name="com.google.android.gms.games.APP_ID"
                android:value="@string/game_services_project_id" />
    ```
    inside \<aplication> and \</aplication>
    > **⚠ NOTE: use the variable from the string don't replace with real value here.**  
5. create or add this in `android/build/res/values/strings.xml`
    ```
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="game_services_project_id" translatable="false"> 0000000000 </string>
    </resources>

    ```
    > Replace 0000000000 with your game’s project id.
6. In Godot Menu, Project -> Export... -> Select Android, Check on Use Custom Build and check on Godot Google Play Game Service v2.
7. You can build and test the game

You can refrence the [godot/](godot/) directory plugins and android build configs sample.

### Some Helpful commands
```
# to view the logcat
$ adb logcat

# to clear the logcat
$ adb logcat -c

# to look for certain tags
$ adb logcat -s godot

# plugin registration
$ adb logcat -s GodotPluginRegistry

```



## Available APIs

| Method Name | Parameters | Return Type | Description                                           |
|-------------|------------|-------------|-------------------------------------------------------|
| firstTest   | -          | -           | Just a test function to make sure the plugin is configured correctly. Upon called this emitts the `on_first_test` signal  |
| initialize   | -          | -           | This function is used to initalize the play service, place it on autoload and call on _ready. It will try to auto login the user. It will send the auto login result through  the `on_sign_in` signal  |
| get_profile   | -          | -           | emits `on_player_info` signal  |
| isAuthenticated   | -          | boolean           | to check if user is authenticated  |
| manualSignIn   | -          | -           | to trigger manual signin, in case autologin is failed, show a button to user to manually singin. emits `on_manual_sign_in` signal |
| requestOAuthAuthCode   | string (OAuth2WebClient)          | -           | to request authcode for current loggined user. emits `on_oauth_auth_code` signal  |
| achievement_set_step   | string, int (achievementID, steps)          | -           | fire-and-forget form of the API.  |
| achievement_revele   | string (achievementID)          | -           | fire-and-forget form of the API.  |
| achievement_unlock   | string (achievementID)          | -           | fire-and-forget form of the API.  |
| show_achievement   | -         | -           | It shows achievements on top of the game, (startActivityForResult). emits `on_achievement_shown` signal  |
| send_event   | string, int (eventId, amount)          | -           | fire-and-forget form of the API.  |
| show_leaderboard   | string (leaderboardId)         | -           | It shows leaderboard on top of the game, (startActivityForResult). emits `on_leaderboard_shown` signal  |
| get_leaderboard_score   | string, int, int (leaderboardId, span, collection)         | -           | to get the score of the logined player in that leaderboard for that span of time and that collection. emits `on_leaderboard_shown` signal.   |


### Possible values for span
| value       | name                    |
|-------------|-------------------------|
| 0           | TIME_SPAN_DAILY         | 
| 1           | TIME_SPAN_WEEKLY        | 
| 2           | TIME_SPAN_ALL_TIME      | 


### Possible values for collection
| value       | name                    |
|-------------|-------------------------|
| 0           | COLLECTION_PUBLIC         | 
| 3           | COLLECTION_FRIENDS        | 


## Available Signals
| signal | callback parameters | Description |
|--------|---------------------|-------------|
| on_first_test | boolean | always true |
| on_sign_in | boolean (success) | to indicate whether auto login succed or failed |
| on_player_info | string (playerProfile - JSON stringified) | stringified player object |
| on_manual_sign_in | boolean (success) | whether manual signin succeed or failed |
| on_oauth_auth_code | boolean, string (success, authcode) | whether succeed or failed, if success true and the code, if failed false and empty string |
| on_achievement_shown | boolean (success) | whether shown or not |
| on_leaderboard_score | boolean, int (success, score) | whether succeed or failed and if succeed the score, else score is zero |
| on_leaderboard_shown | boolean (success) | whether leader board is show or not |

## use in godot
A gdscript is included in [godot/scripts/playservice.gd](godot/scripts/playservice.gd) take a reference of that.

## Future of the plugin
This plugin is released as it is, assuming it will provide some headstart while writing your own.

But if you find the plugin usefull and want to have some additional apis, let us know through the github issue, we can implement that.

## Firebase
We would like to continue extending this plugin for our use case, We will be adding Firebase. Learn more at [FIREBASE.md](FIREBASE.md).

## Thanks to
While developing this plugin I took lots of refrences from plugins these people has maintained for godot.
- [Shin-NiL](https://github.com/Shin-NiL)
- [Constantin Gisca](https://github.com/cgisca)
- [Randy Tan](https://github.com/oneseedfruit)
- [Mitch](https://github.com/finepointcgi)
