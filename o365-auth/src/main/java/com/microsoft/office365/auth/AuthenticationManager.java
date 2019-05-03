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
import com.microsoft.graph.authentication.MSALAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

import java.io.IOException;

public class AuthenticationManager implements IAuthenticationProvider{

    private static final String USER_ID_VAR_NAME = "userId";

    private static final int PREFERENCES_MODE = Context.MODE_PRIVATE;

    private final Activity mActivity;

    private final PublicClientApplication mPublicClientApplication;

    private static final String TAG = "AuthenticationManager";

    private AuthenticationResult mAuthResult;

    private MSALAuthenticationCallback mActivityCallback;

    public static  MSALAuthenticationProvider msalAuthenticationProvider;

    private final String
            mAuthenticationResourceId,
            mSharedPreferencesFilename,
            mClientId,
            mRedirectUri;
     private final String[] mScopes;

    AuthenticationManager(
            Activity activity,
            PublicClientApplication publicClientApplication,
            String authenticationResourceId,
            String sharedPreferencesFilename,
            String clientId,
            String redirectUri,
            String[] scopes) {
        mActivity = activity;
        mPublicClientApplication = publicClientApplication;
        mAuthenticationResourceId = authenticationResourceId;
        mSharedPreferencesFilename = sharedPreferencesFilename;
        mClientId = clientId;
        mRedirectUri = redirectUri;
        mScopes = scopes;
    }

    private SharedPreferences getSharedPreferences() {
        return mActivity.getSharedPreferences(mSharedPreferencesFilename, PREFERENCES_MODE);
    }

    /**
     * Returns the access token obtained in authentication
     *
     * @return mAccessToken
     */
    public String getAccessToken() throws AuthenticatorException, IOException, OperationCanceledException {
        return  mAuthResult.getAccessToken();
    }

    public PublicClientApplication getPublicClient(){
        return mPublicClientApplication;
    }

    /**
     * Disconnects the app from Office 365 by clearing the token cache, setting the client objects
     * to null, and removing the user id from shred preferences.
     */
    public void disconnect() {
        mPublicClientApplication.removeAccount(mAuthResult.getAccount());

        // Forget the user
        removeUserId();
    }

    /**
     * Authenticates the user and lets the user authorize the app for the requested permissions.
     * An authentication token is returned via the getAuthInteractiveCallback method
     * @param authenticationCallback
     */
    public void callAcquireToken(final MSALAuthenticationCallback authenticationCallback) {
        mActivityCallback = authenticationCallback;
        mPublicClientApplication.acquireToken(
                mActivity, mScopes, getAuthInteractiveCallback());
    }
    public void callAcquireTokenSilent(IAccount user, boolean forceRefresh, MSALAuthenticationCallback msalAuthenticationCallback) {
        mActivityCallback = msalAuthenticationCallback;
        mPublicClientApplication.acquireTokenSilentAsync(mScopes, user, null, forceRefresh, getAuthSilentCallback());
    }

// App callbacks for MSAL
// ======================
// getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
// getAuthInteractiveCallback() - callback defined to handle acquireToken() case

    /* Callback method for acquireTokenSilent calls
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */

    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                mAuthResult = authenticationResult;

                //invoke UI callback
                if (mActivityCallback != null)
                    mActivityCallback.onSuccess(mAuthResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                if (mActivityCallback != null)
                    mActivityCallback.onError(exception);
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                mAuthResult = authenticationResult;
                if (mActivityCallback != null)
                    mActivityCallback.onSuccess(mAuthResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                if (mActivityCallback != null)
                    mActivityCallback.onError(exception);
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
                if (mActivityCallback != null)
                    mActivityCallback.onCancel();
            }
        };
    }

    @Override
    public void authenticateRequest(IHttpRequest request) {
        try {
            request.addHeader("Authorization", "Bearer "
                    + this
                    .getAccessToken());
            // This header has been added to identify this sample in the Microsoft Graph service.
            // If you're using this code for your project please remove the following line.
            request.addHeader("SampleID", "android-java-snippets-rest-sample");

            Log.i("Connect","Request: " + request.toString());
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

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