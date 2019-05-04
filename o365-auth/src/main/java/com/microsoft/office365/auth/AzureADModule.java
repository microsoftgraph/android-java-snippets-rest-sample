/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.auth;

import android.app.Activity;

import com.microsoft.identity.client.PublicClientApplication;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AzureADModule {

    private final Builder mBuilder;

    protected AzureADModule(Builder builder) {
        mBuilder = builder;
    }

    @Provides
    @SuppressWarnings("unused") // not actually unused -- used by Dagger
    public PublicClientApplication providesPublicClientApplication() {
        return new PublicClientApplication(
                mBuilder.mActivity,
                mBuilder.mClientId);
    }

    @Provides
    @SuppressWarnings("unused") // not actually unused -- used by Dagger
    public AuthenticationManager providesAuthenticationManager(
            PublicClientApplication publicClientApplication) {
        return new AuthenticationManager(
                mBuilder.mActivity,
                publicClientApplication,
                mBuilder.mClientId,
                mBuilder.mScopes);
    }

    public static class Builder {

        private static final String SHARED_PREFS_DEFAULT_NAME = "AzureAD_Preferences";

        private Activity mActivity;

        private String
                mAuthorityUrl,
                mClientId;
        private String[] mScopes;

        private boolean mValidateAuthority = true;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder authorityUrl(String authorityUrl) {
            mAuthorityUrl = authorityUrl;
            return this;
        }

        public Builder validateAuthority(boolean shouldEvaluate) {
            mValidateAuthority = shouldEvaluate;
            return this;
        }

        public Builder clientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public Builder scopes(String[] scopes){
            mScopes = scopes;
            return this;
        }

        public AzureADModule build() {
            if (null == mAuthorityUrl) {
                throw new IllegalStateException("authorityUrl() is unset");
            }
            if (null == mClientId) {
                throw new IllegalStateException("clientId() is unset");
            }
            if(null == mScopes){
                throw new IllegalStateException("scopes() is unset");
            }
            return new AzureADModule(this);
        }
    }
}