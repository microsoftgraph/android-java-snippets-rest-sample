package com.lnikkila.oidc.security;

/**
 * Wrapper for {@link android.security.keystore.UserNotAuthenticatedException} because it does not
 * exist in pre M APIs.
 * Created by Camilo Montes on 20/01/2016.
 */
public class UserNotAuthenticatedWrapperException extends Exception {

    public UserNotAuthenticatedWrapperException(Throwable throwable) {
        super(throwable);
    }
}
