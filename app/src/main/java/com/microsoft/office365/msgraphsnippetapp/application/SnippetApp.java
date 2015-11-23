/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.application;

import android.app.Application;

import com.microsoft.office365.msgraphsnippetapp.BuildConfig;
import com.microsoft.office365.msgraphsnippetapp.inject.AppModule;

import javax.inject.Inject;

import dagger.ObjectGraph;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import timber.log.Timber;

public class SnippetApp extends Application {
    private static SnippetApp sSnippetApp;
    /**
     * The {@link dagger.ObjectGraph} used by Dagger to fulfill <code>@inject</code> annotations
     *
     * @see javax.inject.Inject
     * @see dagger.Provides
     * @see javax.inject.Singleton
     */
    public ObjectGraph mObjectGraph;
    @Inject
    protected String endpoint;

    @Inject
    protected RestAdapter.LogLevel logLevel;

    @Inject
    protected RequestInterceptor requestInterceptor;

    public static SnippetApp getApp() {
        return sSnippetApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sSnippetApp = this;
        mObjectGraph = ObjectGraph.create(new AppModule());
        mObjectGraph.inject(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public RestAdapter getRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(logLevel)
                .setRequestInterceptor(requestInterceptor)
                .build();
    }
}