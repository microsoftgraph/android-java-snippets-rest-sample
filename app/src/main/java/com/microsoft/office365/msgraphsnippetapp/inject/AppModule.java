/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
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
// *********************************************************
//
// O365-Android-Microsoft-Graph-Snippets, https://github.com/OfficeDev/O365-Android-Microsoft-Graph-Snippets
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// *********************************************************
