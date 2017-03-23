package com.lnikkila.oidc.security;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.io.IOException;

/**
 * Created by Camilo Montes on 18/01/2016. <br/>
 * Copyright LaPoste <br/>
 */
public class AccountSensitiveDataStorageUtils {

    private final static boolean SHOW_NOTIF_ON_AUTHFAILURE = true;
    private SensitiveDataUtils dataEncUtils;

    public AccountSensitiveDataStorageUtils(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            dataEncUtils = new SensitiveDataPreApi23(context);
        } else {
            dataEncUtils = new SensitiveDataPostApi23(context);
        }
    }

    /**
     * Get the stored data from a secure store, decrypting the data if needed.
     * @return The data store on the secure storage.
     */
    public String retrieveStringData(AccountManager accountManager, Account account, String tokenType, AccountManagerCallback<Bundle> callback)
            throws UserNotAuthenticatedWrapperException, AuthenticatorException, OperationCanceledException, IOException {
        String data = null;

        // Try retrieving an access token from the account manager. The boolean #SHOW_NOTIF_ON_AUTHFAILURE in the invocation
        // tells Android to show a notification if the token can't be retrieved. When the
        // notification is selected, it will launch the intent for re-authorisation. You could
        // launch it automatically here if you wanted to by grabbing the intent from the bundle.
        AccountManagerFuture<Bundle> futureManager;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //noinspection deprecation
            futureManager = accountManager.getAuthToken(
                    account,
                    tokenType,
                    SHOW_NOTIF_ON_AUTHFAILURE,
                    callback,
                    null);
        }
        else {
            futureManager = accountManager.getAuthToken(
                    account,
                    tokenType,
                    null,
                    SHOW_NOTIF_ON_AUTHFAILURE,
                    callback,
                    null);
        }
        String encryptedToken = futureManager.getResult().getString(AccountManager.KEY_AUTHTOKEN);
        if (encryptedToken != null) {
            data = dataEncUtils.decrypt(encryptedToken);
        }

        return data;
    }

    /**
     * Store the given serialized data onto a secure store, encrypting the data if needed.
     * @param data The data to store securely
     * @return true if the data was store, false otherwise.
     */
    public boolean storeStringData(AccountManager accountManager, Account account, String tokenType, String data) throws UserNotAuthenticatedWrapperException {
        accountManager.setAuthToken(account, tokenType, dataEncUtils.encrypt(data));
        return true;
    }

    public void invalidateStringData(AccountManager accountManager, Account account, String data) throws UserNotAuthenticatedWrapperException {
        accountManager.invalidateAuthToken(account.type, dataEncUtils.encrypt(data));
    }
}
