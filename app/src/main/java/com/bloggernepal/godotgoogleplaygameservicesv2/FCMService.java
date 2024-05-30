package com.bloggernepal.godotgoogleplaygameservicesv2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class FCMService extends FirebaseMessagingService {
    final String TAG = "godot GPGSv2 FCMService";

    public FCMService() {
        super();
        Helper.getInstance().setFcmService(this);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        Helper.getInstance().getPlayService().new_fcm_token_generated(token);
    }
}