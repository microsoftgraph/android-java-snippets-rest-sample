package com.lnikkila.oidc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.util.Preconditions;
import com.lnikkila.oidc.authenticator.Authenticator;
import com.lnikkila.oidc.security.AccountSensitiveDataStorageUtils;
import com.lnikkila.oidc.security.UserNotAuthenticatedWrapperException;

import java.io.IOException;

/**
 * A layer of syntactic sugar around the AccountManager and the Accounts.
 * @author Camilo Montes
 * @since 20/01/2016.
 */
public class OIDCAccountManager {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private final AccountManager manager;
    private final AccountSensitiveDataStorageUtils secureStorage;


    public OIDCAccountManager(Context context) {
        this.context = Preconditions.checkNotNull(context);
        this.manager = AccountManager.get(this.context);
        this.secureStorage = new AccountSensitiveDataStorageUtils(context);
    }

    public AccountManager getAccountManager() {
        return this.manager;
    }

    public String getAccountType() {
        return context.getString(R.string.account_authenticator_type);
    }

    public Account[] getAccounts() {
        return this.manager.getAccountsByType(getAccountType());
    }

    public Account getAccountByName(String accountName) {
        if(accountName != null) {
            Account[] accounts = this.getAccounts();
            for (Account account : accounts) {
                if (accountName.equals(account.name)) {
                    return account;
                }
            }
        }
        return null;
    }

    public void createAccount(Activity activity, AccountManagerCallback<Bundle> callback) {
        this.manager.addAccount(getAccountType(), Authenticator.TOKEN_TYPE_ID, null, null, activity, callback, null);
    }

    public boolean removeAccount(String accountName) {
        Account account = getAccountByName(accountName);
        return removeAccount(account);
    }

    public boolean removeAccount(Account account) {
        boolean removed = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
            removed = this.manager.removeAccountExplicitly(account);
        }
        else {
            @SuppressWarnings("deprecation") AccountManagerFuture<Boolean> futureRemoved = this.manager.removeAccount(account, null, null);
            try {
                removed = futureRemoved.getResult();
            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                Log.w("LogoutTask", "Coudln't remove account using pre LOLIPOP remove call");
            }
        }
        return removed;
    }

    public boolean isKeyPinRequired() {
        boolean keyPinRequired = false;
        if (context != null) {
            keyPinRequired = context.getResources().getBoolean(R.bool.oidc_encryptKeyAskPin);
        }
        return keyPinRequired;
    }

    public void invalidateAllAccountTokens(Account account) {
        if(account != null) {
            try {
                String idToken = getIdToken(account.name, null);
                String accessToken = getAccessToken(account.name, null);
                String refreshToken = getRefreshToken(account.name, null);
                this.secureStorage.invalidateStringData(this.manager, account, idToken);
                this.secureStorage.invalidateStringData(this.manager, account, accessToken);
                this.secureStorage.invalidateStringData(this.manager, account, refreshToken);
            } catch (AuthenticatorException | UserNotAuthenticatedWrapperException | OperationCanceledException | IOException e) {
                Log.w(TAG, String.format("Could not invalidate account %1$s tokens", account.name), e);
            }
        }
    }

    public void invalidateAuthTokens(Account account) {
        if(account != null) {
            try {
                String idToken = getIdToken(account.name, null);
                String accessToken = getAccessToken(account.name, null);
                this.secureStorage.invalidateStringData(this.manager, account, idToken);
                this.secureStorage.invalidateStringData(this.manager, account, accessToken);
            } catch (AuthenticatorException | UserNotAuthenticatedWrapperException | OperationCanceledException | IOException e) {
                Log.w(TAG, String.format("Could not invalidate account %1$s tokens", account.name), e);
            }
        }
    }


    public void invalidateAccessToken(Account account) {
        if(account != null) {
            try {
                String accessToken = getAccessToken(account, null);
                this.secureStorage.invalidateStringData(this.manager, account, accessToken);
            } catch (AuthenticatorException | UserNotAuthenticatedWrapperException | OperationCanceledException | IOException e) {
                Log.w(TAG, String.format("Could not invalidate account %1$s AT", account.name), e);
            }
        }
    }

    public String getIdToken(String accountName, AccountManagerCallback<Bundle> callback)
            throws AuthenticatorException, UserNotAuthenticatedWrapperException, OperationCanceledException, IOException {
        return getToken(accountName, Authenticator.TOKEN_TYPE_ID, callback);
    }

    public String getAccessToken(Account account, AccountManagerCallback<Bundle> callback)
            throws AuthenticatorException, UserNotAuthenticatedWrapperException, OperationCanceledException, IOException {
        return getToken(account, Authenticator.TOKEN_TYPE_ACCESS, callback);
    }

    public String getAccessToken(String accountName, AccountManagerCallback<Bundle> callback)
            throws AuthenticatorException, UserNotAuthenticatedWrapperException, OperationCanceledException, IOException {
        return getToken(accountName, Authenticator.TOKEN_TYPE_ACCESS, callback);
    }

    public String getRefreshToken(String accountName, AccountManagerCallback<Bundle> callback)
            throws AuthenticatorException, UserNotAuthenticatedWrapperException, OperationCanceledException, IOException {
        return getToken(accountName, Authenticator.TOKEN_TYPE_REFRESH, callback);
    }

    private String getToken(String accountName, String tokenType, AccountManagerCallback<Bundle> callback)
            throws AuthenticatorException, UserNotAuthenticatedWrapperException, OperationCanceledException, IOException {
        Account account = getAccountByName(accountName);
        return getToken(account, tokenType, callback);
    }

    private String getToken(Account account, String tokenType, AccountManagerCallback<Bundle> callback)
            throws AuthenticatorException, UserNotAuthenticatedWrapperException, OperationCanceledException, IOException {
        return this.secureStorage.retrieveStringData(this.manager, account, tokenType, callback);
    }

    public void saveTokens(Account account, TokenResponse tokenResponse) throws UserNotAuthenticatedWrapperException {
        if (tokenResponse instanceof IdTokenResponse) {
            saveToken(account, Authenticator.TOKEN_TYPE_ID, ((IdTokenResponse) tokenResponse).getIdToken());
        }
        saveToken(account, Authenticator.TOKEN_TYPE_ACCESS, tokenResponse.getAccessToken());
        saveToken(account, Authenticator.TOKEN_TYPE_REFRESH, tokenResponse.getRefreshToken());
    }

    public void saveTokens(String accountName, TokenResponse tokenResponse) throws UserNotAuthenticatedWrapperException {
        if (tokenResponse instanceof IdTokenResponse) {
            saveToken(accountName, Authenticator.TOKEN_TYPE_ID, ((IdTokenResponse)tokenResponse).getIdToken());
        }
        saveToken(accountName, Authenticator.TOKEN_TYPE_ACCESS, tokenResponse.getAccessToken());
        saveToken(accountName, Authenticator.TOKEN_TYPE_REFRESH, tokenResponse.getRefreshToken());
    }

    private void saveToken(String accountName, String tokenType, String token) throws UserNotAuthenticatedWrapperException {
        Account account = getAccountByName(accountName);
        saveToken(account, tokenType, token);
    }

    private void saveToken(Account account, String tokenType, String token) throws UserNotAuthenticatedWrapperException {
        this.secureStorage.storeStringData(this.manager, account, tokenType, token);
    }
}
