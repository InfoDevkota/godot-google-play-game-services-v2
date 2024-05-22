# Godot Google Play Game Services V2

This Plugin is developed for one of our game [Carrom Karrom: Carrom Board](https://play.google.com/store/apps/details?id=com.bloggernepal.carrom), you can check the game for live demo. Our requirements may not be enough for your need but, you can modify to fit your need check [DEVELOPMENT.md](DEVELOPMENT.md)

With this plugin you can

Add Play Games services to your game to get frictionless zero-click sign-in

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