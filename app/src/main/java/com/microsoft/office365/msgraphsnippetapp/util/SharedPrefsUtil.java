/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;
import com.microsoft.office365.msgraphsnippetapp.inject.AppModule;

public class SharedPrefsUtil {

    public static final String PREF_AUTH_TOKEN = "PREF_AUTH_TOKEN";
    public static final String PREF_USER_TENANT = "PREF_USER_TENANT";
    public static final String PREF_USER_ID = "PREF_USER_ID";

    public static SharedPreferences getSharedPreferences() {
        return SnippetApp.getApp().getSharedPreferences(AppModule.PREFS, Context.MODE_PRIVATE);
    }

    public static void persistUserID(AuthenticationResult result) {
        setPreference(PREF_USER_ID, result.getUserInfo().getDisplayableId());
    }

    public static void persistAuthToken(AuthenticationResult result) {
        setPreference(PREF_AUTH_TOKEN, result.getAccessToken());
    }

    public static void persistUserTenant(String tenant) {
        getSharedPreferences().edit().putString(PREF_USER_TENANT, tenant).commit();
    }

    private static void setPreference(String key, String accessToken) {
        getSharedPreferences().edit().putString(key, accessToken).commit();
    }

}