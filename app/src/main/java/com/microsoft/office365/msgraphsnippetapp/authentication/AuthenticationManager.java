package com.microsoft.office365.msgraphsnippetapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lnikkila.oidc.OIDCAccountManager;
import com.lnikkila.oidc.OIDCRequestManager;
import com.lnikkila.oidc.security.UserNotAuthenticatedWrapperException;

import java.io.IOException;

/**
 * Handles setup of ADAL Dependency Resolver for use in API clients.
 */

public class AuthenticationManager {

    private static final String TAG = "AuthenticationManager";
    private static AuthenticationManager INSTANCE;

    private OIDCAccountManager mOIDCAccountManager;

    private AuthenticationManager() {
    }

    public static synchronized AuthenticationManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationManager();
            INSTANCE.mOIDCAccountManager = new OIDCAccountManager(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences("oidc_clientconf", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("oidc_loadfromprefs", true);

            //Was false. When false, exception is thrown in OIDCRequestManager, line 378. id is null
            editor.putBoolean("oidc_oauth2only", false);
            editor.putString("oidc_clientId", Constants.CLIENT_ID);
            editor.putString("oidc_redirectUrl", Constants.REDIRECT_URI);
            editor.putString("oidc_scopes", Constants.SCOPES);
            editor.putString("oidc_flowType", OIDCRequestManager.Flows.Code.name());

            editor.apply();
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
    public String getAccessToken() throws AuthenticatorException, IOException, OperationCanceledException, UserNotAuthenticatedWrapperException {
        return mOIDCAccountManager.getAccessToken(mOIDCAccountManager.getAccounts()[0], null);
    }

    /**
     *
     * @param authenticationCallback The callback to notify when the processing is finished.
     */
    public void connect(Activity activity, final AuthenticationCallback<String, String> authenticationCallback) {
        switch (mOIDCAccountManager.getAccounts().length) {
            // No account has been created, let's create one now
            case 0:
                mOIDCAccountManager.createAccount(activity, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> futureManager) {
                        // Unless the account creation was cancelled, try logging in again
                        // after the account has been created.
                        if (!futureManager.isCancelled()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Account account = mOIDCAccountManager.getAccounts()[0];
                                    try {
                                        String authToken = mOIDCAccountManager.getAccessToken(account, null);

                                        authenticationCallback.onSuccess(mOIDCAccountManager.getIdToken(account.name, null)
                                                , authToken);
                                    } catch (AuthenticatorException | UserNotAuthenticatedWrapperException | OperationCanceledException | IOException | NullPointerException e) {
                                        authenticationCallback.onError(e);
                                    }
                                }
                            }).start();
                        } else {
                            authenticationCallback.onError(new AuthenticatorException("Flow was canceled"));
                        }
                    }
                });
                break;
            case 1:
                // if we have an user endpoint we try to get userinfo with the receive token
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Account account = mOIDCAccountManager.getAccounts()[0];
                        try {
                            String authToken = mOIDCAccountManager.getAccessToken(account, null);
                            authenticationCallback.onSuccess(mOIDCAccountManager.getIdToken(account.name, null)
                                    , authToken);
                        } catch (AuthenticatorException | UserNotAuthenticatedWrapperException | OperationCanceledException | IOException | NullPointerException e) {
                            authenticationCallback.onError(e);
                        }
                    }
                }).start();
                break;
        }
    }

    /**
     * Disconnects the app from Office 365 by clearing the token cache, setting the client objects
     * to null, and removing the user id from shred preferences.
     */
    public void disconnect() {
        mOIDCAccountManager.removeAccount(mOIDCAccountManager.getAccounts()[0]);
        // Reset the AuthenticationManager object
        AuthenticationManager.resetInstance();
    }
}