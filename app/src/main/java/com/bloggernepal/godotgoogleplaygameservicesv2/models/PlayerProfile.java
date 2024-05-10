package com.bloggernepal.godotgoogleplaygameservicesv2.models;

import com.google.gson.annotations.SerializedName;

public class PlayerProfile {
    @SerializedName("player_id")
    private String playerId;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("title")
    private String title;


    public PlayerProfile(String playerId, String displayName, String title) {
        this.playerId = playerId;
        this.displayName = displayName;
        this.title = title;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTitle() {
        return title;
    }

}
