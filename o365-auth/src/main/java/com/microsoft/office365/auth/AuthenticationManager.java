/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.auth;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.MSALAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.snippets.application.SnippetApp;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;


import java.io.IOException;

/**
 * Handles setup of OAuth library in API clients.
 */
public class AuthenticationManager implements IAuthenticationProvider {
    private static final String TAG = "AuthenticationManager";
    private static AuthenticationManager INSTANCE;
    private static PublicClientApplication mPublicClientApplication;
    private AuthenticationResult mAuthResult;
    private MSALAuthenticationCallback mActivityCallback;
    public static  MSALAuthenticationProvider msalAuthenticationProvider;

    public AuthenticationManager() {
    }

    public static synchronized AuthenticationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationManager();
            if (mPublicClientApplication == null) {
                mPublicClientApplication = new PublicClientApplication(SnippetApp.getContext(), "");
            }
        }
        return INSTANCE;
    }

    public static synchronized void resetInstance() {
        INSTANCE = null;
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
        // Reset the AuthenticationManager object
        AuthenticationManager.resetInstance();
    }

    /**
     * Authenticates the user and lets the user authorize the app for the requested permissions.
     * An authentication token is returned via the getAuthInteractiveCalback method
     * @param activity
     * @param authenticationCallback
     */
    public void callAcquireToken(Activity activity, final MSALAuthenticationCallback authenticationCallback) {
        mActivityCallback = authenticationCallback;
        mPublicClientApplication.acquireToken(
                activity, ServiceConstants.SCOPES, getAuthInteractiveCallback());
    }
    public void callAcquireTokenSilent(IAccount user, boolean forceRefresh, MSALAuthenticationCallback msalAuthenticationCallback) {
        mActivityCallback = msalAuthenticationCallback;
        mPublicClientApplication.acquireTokenSilentAsync(ServiceConstants.SCOPES, user, null, forceRefresh, getAuthSilentCallback());
    }
//
// App callbacks for MSAL
// ======================
// getActivity() - returns activity so we can acquireToken within a callback
// getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
// getAuthInteractiveCallback() - callback defined to handle acquireToken() case
//

    public Context getActivity() {
        return SnippetApp.getContext();
    }

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
                    + AuthenticationManager.getInstance()
                    .getAccessToken());
            // This header has been added to identify this sample in the Microsoft Graph service.
            // If you're using this code for your project please remove the following line.
            request.addHeader("SampleID", "android-java-connect-sample");

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
}
