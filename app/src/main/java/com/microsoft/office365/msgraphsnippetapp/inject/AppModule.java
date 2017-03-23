/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.inject;

import android.content.Context;
import android.content.SharedPreferences;

import com.microsoft.office365.msgraphsnippetapp.ServiceConstants;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;
import com.microsoft.office365.msgraphsnippetapp.authentication.AuthenticationManager;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor.Level;

@Module(library = true,
        injects = {SnippetApp.class}
)
public class AppModule {

    public static final String PREFS = "com.microsoft.o365_android_unified_API_REST_snippets";

    @Provides
    @SuppressWarnings("unused") // not actually unused -- used by Dagger
    public String providesRestEndpoint() {
        return ServiceConstants.AUTHENTICATION_RESOURCE_ID;
    }

    @Provides
    @SuppressWarnings("unused") // not actually unused -- used by Dagger
    public Level providesLogLevel() {
        return Level.BODY;
    }

    @Provides
    @SuppressWarnings("unused") // not actually unused -- used by Dagger
    public Interceptor providesRequestInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // apply the Authorization header if we had a token...
                final SharedPreferences preferences
                        = SnippetApp.getApp().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                final String token =
                        preferences.getString(SharedPrefsUtil.PREF_AUTH_TOKEN, null);

                Request request = chain.request();
                request = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        // This header has been added to identify this sample in the Microsoft Graph service.
                        // If you're using this code for your project please remove the following line.
                        .addHeader("SampleID", "android-java-snippets-rest-sample")
                        .build();

                Response response = chain.proceed(request);
                return response;
            }
        };
    }

}
