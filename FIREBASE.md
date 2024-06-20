# Firebase

## Loading configs from google-services.json
To load the configs from `google-services.json`. This is necessary step. We need to edit the build.gradle in the godot android. On your `[[godot-project]]/android/build/build.gradle` inside `dependencies` of `buildscript` add
```
classpath 'com.google.gms:google-services:4.3.+'
```

Then in the last of the file add.

```
apply plugin: 'com.google.gms.google-services'
```

## Development
### Dependncies version
When we use BOM in don't need to specify version number for other dependent libraries.

But we need them when creating the `.gdap` We can find the associated version from https://firebase.google.com/support/release-notes/android#2024-04-11. If version or any library changed, it will be specified, else no version changed for that particula library for that `bom` so look for previous version release note to get the library version.
