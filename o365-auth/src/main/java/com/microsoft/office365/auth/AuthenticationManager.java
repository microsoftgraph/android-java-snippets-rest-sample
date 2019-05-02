/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.auth;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.PublicClientApplication;

import java.io.IOException;

public class AuthenticationManager{

    private static final String USER_ID_VAR_NAME = "userId";
    private static final String TAG = "AuthenticationManager";
    private static final int PREFERENCES_MODE = Context.MODE_PRIVATE;

    private final Activity mActivity;

    private static AuthenticationManager INSTANCE;
    private static PublicClientApplication mPublicClientApplication;
    private AuthenticationResult mAuthResult;
    private MSALAuthenticationCallback mActivityCallback;
    public static  MSALAuthenticationProvider msalAuthenticationProvider;


    private final String
            mAuthenticationResourceId,
            mSharedPreferencesFilename,
            mClientId,
            mRedirectUri;

    AuthenticationManager(
            Activity activity,
            AuthenticationContext authenticationContext,
            String authenticationResourceId,
            String sharedPreferencesFilename,
            String clientId,
            String redirectUri) {
        mActivity = activity;
        mAuthenticationContext = authenticationContext;
        mAuthenticationResourceId = authenticationResourceId;
        mSharedPreferencesFilename = sharedPreferencesFilename;
        mClientId = clientId;
        mRedirectUri = redirectUri;
    }

    private SharedPreferences getSharedPreferences() {
        return mActivity.getSharedPreferences(mSharedPreferencesFilename, PREFERENCES_MODE);
    }

    public void connect(AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (isConnected()) {
            authenticateSilent(authenticationCallback);
        } else {
            authenticatePrompt(authenticationCallback);
        }
    }

    /**
     * Disconnects the app from Office 365 by clearing the token cache, setting the client objects
     * to null, and removing the user id from shred preferences.
     */
    public void disconnect() {
        // Clear tokens.
        if (mAuthenticationContext.getCache() != null) {
            mAuthenticationContext.getCache().removeAll();
        }

        // Forget the user
        removeUserId();
    }

    private void authenticatePrompt(
            final AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        mAuthenticationContext
                .acquireToken(
                        mAuthenticationResourceId,
                        mClientId,
                        mRedirectUri,
                        null,
                        PromptBehavior.Always,
                        null,
                        new AuthenticationCallback<AuthenticationResult>() {
                            @Override
                            public void onSuccess(final AuthenticationResult authenticationResult) {
                                if (Succeeded == authenticationResult.getStatus()) {
                                    setUserId(authenticationResult.getUserInfo().getUserId());
                                    authenticationCallback.onSuccess(authenticationResult);
                                } else {
                                    onError(
                                            new AuthenticationException(ADALError.AUTH_FAILED,
                                                    authenticationResult.getErrorCode()));
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                disconnect();
                                authenticationCallback.onError(e);
                            }
                        }
                );
    }

    private void authenticateSilent(
            final AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        mAuthenticationContext.acquireTokenSilentAsync(
                mAuthenticationResourceId,
                mClientId,
                getUserId(),
                new AuthenticationCallback<AuthenticationResult>() {
                    @Override
                    public void onSuccess(AuthenticationResult authenticationResult) {
                        authenticationCallback.onSuccess(authenticationResult);
                    }

                    @Override
                    public void onError(Exception e) {
                        authenticatePrompt(authenticationCallback);
                    }
                });
    }

    private boolean isConnected() {
        return getSharedPreferences().contains(USER_ID_VAR_NAME);
    }


    private String getUserId() {
        return getSharedPreferences().getString(USER_ID_VAR_NAME, "");
    }

    private void setUserId(String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(USER_ID_VAR_NAME, value);
        editor.apply();
    }

    private void removeUserId() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(USER_ID_VAR_NAME);
        editor.apply();
    }
}