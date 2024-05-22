extends Node

## This is the the playservice autoload we used on Carrom Karrom
# removed some data and funrtions which are not necessary for the demo
# this code may not work and require your modifications

var _playservice_singleton = null

const ACHIEVEMENT_FIFTY_FEATS = "C****************w";

const WIN_REGISTRY_LEADERBOARD = "C****************A"

const EVENT_LOCAL_COMPLETED = "C****************w";

# To have a secure server side access (so client don't intervin with the data)
# use oauth_code to get the token, use the token to get the playerid, use that player id to get the saved code
# https://developers.google.com/games/services/android/offline-access

# Called when the node enters the scene tree for the first time.
func _ready():
	if(Engine.has_singleton("GodotGooglePlayGameServicesV2")):
		print("GodotGooglePlayGameServicesV2 found")
		_playservice_singleton = Engine.get_singleton("GodotGooglePlayGameServicesV2")
		
		_playservice_singleton.initialize()
		
		_playservice_singleton.connect("on_first_test", self, "on_first_test")
		_playservice_singleton.connect("on_sign_in", self, "on_sign_in")
		_playservice_singleton.connect("on_player_info", self, "on_player_info")
		_playservice_singleton.connect("on_oauth_auth_code", self, "on_oauth_auth_code")
		_playservice_singleton.connect("on_manual_sign_in", self, "on_manual_sign_in")
		
		_playservice_singleton.connect("on_achievement_shown", self, "on_achievement_shown")
		
		_playservice_singleton.connect("on_leaderboard_shown", self, "on_leaderboard_shown")
		_playservice_singleton.connect("on_leaderboard_score", self, "on_leaderboard_score")
		
		
		
	else:
		print("GodotGooglePlayGameServicesV2 Not found")
	pass # Replace with function body.

## helpers
func call_first_test():
	if _playservice_singleton:
		_playservice_singleton.firstTest()
		

func request_oauth_auth_code():
	if _playservice_singleton:
		var oauth_2_web_client_id = "7**********-m******************************e.apps.googleusercontent.com"
		_playservice_singleton.requestOAuthAuthCode(oauth_2_web_client_id)

func manual_sign_in():
	if _playservice_singleton:
		_playservice_singleton.manualSignIn()

func show_achievement():
	if _playservice_singleton:
		_playservice_singleton.show_achievement()

func send_event(event_name, increment=1):
	if _playservice_singleton:
		_playservice_singleton.send_event(event_name, increment)



func player_won():
	# just a dummy function to show achievements and 
	if _playservice_singleton:

		if first_win: # (write logic for this var)
			_playservice_singleton.achievement_revele(ACHIEVEMENT_FIRST_VICTORY)
			_playservice_singleton.achievement_unlock(ACHIEVEMENT_FIRST_VICTORY)

		_playservice_singleton.achievement_set_step(ACHIEVEMENT_FIFTY_FEATS, total_wins); # write logic for this as well

func show_leaderboard(leaderboard):
	if _playservice_singleton:
		_playservice_singleton.show_leaderboard(leaderboard)

func show_win_leaderboard():
	show_leaderboard(WIN_REGISTRY_LEADERBOARD)

func get_leaderboard_score(leaderboard_id, span=0, collection=0):
	if _playservice_singleton:
		_playservice_singleton.get_leaderboard_score(leaderboard_id, span, collection);

func submit_score_to_leaderboard(leaderboard_id, score):
	if _playservice_singleton:
		_playservice_singleton.submit_score_to_leaderboard(leaderboard_id, score)

func is_authenticated():
	if not _playservice_singleton:
		return false;
	return _playservice_singleton.isAuthenticated();
	
## call backs
func on_first_test(reached: bool):
	print("First Test Reached to Androuid");

func on_sign_in(authenticated: bool):
	if authenticated:
		print("Auto Login succeed")
	else:
		print("Auto Login Failed")
	
#	Also we can check 
	var is_authenticated = _playservice_singleton.isAuthenticated()
	print("Again check is_authenticated ", is_authenticated)
	

func on_player_info(player):
	print("got player profile")
	var userProfile = parse_json(player)
	var user_data = Store.get_user_data()
	
	if (
		not user_data.has("already_logined")
		or (
			user_data.has("already_logined") 
			and not user_data.already_logined
		)
	):
		print("this is first login")
#		If has not already loggined
#		let's update the user data and set the name
		user_data.player_name = userProfile.display_name
		user_data.already_logined = true
		user_data.player_id = userProfile["player_id"]
		user_data.name_set = true
		
		Store.save_user_data(user_data)
		
	else:
		# nothing
		print("not first login")
	
	# let's sync saved game local and remote
	sync_score()

func sync_score():
	# read score from local
	# read score from remote and sync
	pass

func on_oauth_auth_code(success: bool, code: String):
	print("Got oauth auth code ", success , " " ,code)

func on_manual_sign_in(success: bool):
	print("Manaul signup status: ", success)
#	if succeed get profile and stuffs
	if success:
		_playservice_singleton.get_profile();

func on_achievement_shown(success: bool):
	print("show achievement return")

func on_leaderboard_shown(success: bool):
	print("show achievement returns")

func on_leaderboard_score(success: bool, score:int):
	if success:
		print("got score back: success: ", score)
	else:
		print("error on getting leaderboard score back")

# Called every frame. 'delta' is the elapsed time since the previous frame.
#func _process(delta):
#	pass


