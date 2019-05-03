/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.auth;

import android.app.Activity;

import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.aad.adal.AuthenticationSettings;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AzureADModule {

    private final Builder mBuilder;

    protected AzureADModule(Builder builder) {
        mBuilder = builder;
    }

    public static void skipBroker(boolean shouldSkip) {
        AuthenticationSettings.INSTANCE.setSkipBroker(shouldSkip);
    }

    @Provides
    @SuppressWarnings("unused") // not actually unused -- used by Dagger
    public PublicClientApplication providesAuthenticationContext() {
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
                mBuilder.mAuthenticationResourceId,
                mBuilder.mSharedPreferencesFilename,
                mBuilder.mClientId,
                mBuilder.mRedirectUri,
                mBuilder.mScopes);
    }

    public static class Builder {

        private static final String SHARED_PREFS_DEFAULT_NAME = "AzureAD_Preferences";

        private Activity mActivity;

        private String
                mAuthorityUrl, // the authority used to authenticate
                mAuthenticationResourceId, // the resource id used to authenticate
                mSharedPreferencesFilename = SHARED_PREFS_DEFAULT_NAME,
                mClientId,
                mRedirectUri;
        private String[] mScopes;

        private boolean mValidateAuthority = true;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder authorityUrl(String authorityUrl) {
            mAuthorityUrl = authorityUrl;
            return this;
        }

        public Builder authenticationResourceId(String authenticationResourceId) {
            mAuthenticationResourceId = authenticationResourceId;
            return this;
        }

        public Builder validateAuthority(boolean shouldEvaluate) {
            mValidateAuthority = shouldEvaluate;
            return this;
        }

        public Builder skipBroker(boolean shouldSkip) {
            AzureADModule.skipBroker(shouldSkip);
            return this;
        }

        public Builder sharedPreferencesFilename(String filename) {
            mSharedPreferencesFilename = filename;
            return this;
        }

        public Builder clientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            mRedirectUri = redirectUri;
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
            if (null == mAuthenticationResourceId) {
                throw new IllegalStateException("authenticationResourceId() is unset");
            }
            if (null == mSharedPreferencesFilename) {
                mSharedPreferencesFilename = SHARED_PREFS_DEFAULT_NAME;
            }
            if (null == mClientId) {
                throw new IllegalStateException("clientId() is unset");
            }
            if (null == mRedirectUri) {
                throw new IllegalStateException("redirectUri() is unset");
            }
            if(null == mScopes){
                throw new IllegalStateException("scopes() is unset");
            }
            return new AzureADModule(this);
        }

    }

}