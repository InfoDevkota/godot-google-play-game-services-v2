extends Node


var _playservice_singleton = null


# Called when the node enters the scene tree for the first time.
func _ready():
	if(Engine.has_singleton("GodotGooglePlayGameServicesV2")):
		print("GodotGooglePlayGameServicesV2 found")
		_playservice_singleton = Engine.get_singleton("GodotGooglePlayGameServicesV2")
		
		# init test
		_playservice_singleton.firstTest()
	else:
		print("GodotGooglePlayGameServicesV2 Not found")
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
#func _process(delta):
#	pass
