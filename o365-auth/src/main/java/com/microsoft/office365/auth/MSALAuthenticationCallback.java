package com.microsoft.office365.auth;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

public interface MSALAuthenticationCallback {
    void onSuccess(AuthenticationResult authenticationResult);
    void onError(MsalException exception);
    void onError(Exception exception);
    void onCancel();
}
