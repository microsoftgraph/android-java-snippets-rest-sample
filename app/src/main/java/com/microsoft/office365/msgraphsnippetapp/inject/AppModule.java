/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.inject;

import android.content.Context;
import android.content.SharedPreferences;

import com.microsoft.office365.msgraphsnippetapp.ServiceConstants;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module(library = true,
        injects = {SnippetApp.class}
)
public class AppModule {

    public static final String PREFS = "com.microsoft.o365_android_unified_API_REST_snippets";

    @Provides
    public String providesRestEndpoint() {
        return ServiceConstants.AUTHENTICATION_RESOURCE_ID;
    }

    @Provides
    public RestAdapter.LogLevel providesLogLevel() {
        return RestAdapter.LogLevel.FULL;
    }

    @Provides
    public RequestInterceptor providesRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                // apply the Authorization header if we had a token...
                final SharedPreferences preferences
                        = SnippetApp.getApp().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                final String token =
                        preferences.getString(SharedPrefsUtil.PREF_AUTH_TOKEN, null);
                if (null != token) {
                    request.addHeader("Authorization", "Bearer " + token);
                }
            }
        };
    }

}
