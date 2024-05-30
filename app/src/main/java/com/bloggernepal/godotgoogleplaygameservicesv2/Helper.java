package com.bloggernepal.godotgoogleplaygameservicesv2;

public class Helper {
    private static Helper _helper;
    public static Helper getInstance() {
        if (_helper == null) {
            _helper = new Helper();
        }
        return _helper;
    }

    PlayService playService;
    FCMService fcmService;

    public PlayService getPlayService() {
        return playService;
    }

    public void setPlayService(PlayService playService) {
        this.playService = playService;
    }

    public FCMService getFcmService() {
        return fcmService;
    }

    public void setFcmService(FCMService fcmService) {
        this.fcmService = fcmService;
    }
}
