package com.example.cscan.models;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenModel {
    private static final String PREF_NAME = "TokenPrefs";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";

    private String accessToken;
    private String refreshToken;

    // Constructor mặc định
    public TokenModel() {
    }

    // Constructor với tham số
    public TokenModel(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Lưu TokenModel vào SharedPreferences
    public void saveTokenModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    // Tải TokenModel từ SharedPreferences
    public static TokenModel loadTokenModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        String refreshToken = sharedPreferences.getString(KEY_REFRESH_TOKEN, null);

        return new TokenModel(accessToken, refreshToken);
    }
}
