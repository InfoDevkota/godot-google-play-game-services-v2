extends Node


var _playservice_singleton = null


# Called when the node enters the scene tree for the first time.
func _ready():
	if(Engine.has_singleton("GodotGooglePlayGameServicesV2")):
		print("GodotGooglePlayGameServicesV2 found")
		_playservice_singleton = Engine.get_singleton("GodotGooglePlayGameServicesV2")

		_playservice_singleton.initialize()

		_playservice_singleton.connect("on_first_test", self, "on_first_test")
		_playservice_singleton.connect("on_sign_in", self, "on_sign_in")
		_playservice_singleton.connect("on_player_info", self, "on_player_info")
	else:
		print("GodotGooglePlayGameServicesV2 Not found")
	pass # Replace with function body.

## helpers
func call_first_test():
	if _playservice_singleton:
		_playservice_singleton.firstTest()



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



# Called every frame. 'delta' is the elapsed time since the previous frame.
#func _process(delta):
#	pass
