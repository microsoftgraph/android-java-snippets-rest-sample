/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import java.net.URI;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity
        extends BaseActivity
        implements AuthenticationCallback<AuthenticationResult> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ButterKnife.inject(this);
    }

    @OnClick(R.id.o365_signin)
    public void onSignInO365Clicked() {
        try {
            authenticateOrganization();
        } catch (IllegalArgumentException e) {
            warnBadClient();
        }
    }

    private void warnBadClient() {
        Toast.makeText(this,
                R.string.warning_clientid_redirecturi_incorrect,
                Toast.LENGTH_LONG)
                .show();
    }

    private void authenticateOrganization() throws IllegalArgumentException {
        validateOrganizationArgs();
        mAuthenticationManager.connect(this);
    }

    private void validateOrganizationArgs() throws IllegalArgumentException {
        UUID.fromString(ServiceConstants.CLIENT_ID);
        URI.create(ServiceConstants.REDIRECT_URI);
    }

    @Override
    public void onSuccess(AuthenticationResult authenticationResult) {
        finish();
        SharedPrefsUtil.persistAuthToken(authenticationResult);
        int at = authenticationResult.getUserInfo().getDisplayableId().indexOf("@");
        String tenant = authenticationResult.getUserInfo().getDisplayableId().substring(at + 1);
        SharedPrefsUtil.persistUserTenant(tenant);
        SharedPrefsUtil.persistUserID(authenticationResult);
        start();
    }

    private void start() {
        Intent appLaunch = new Intent(this, SnippetListActivity.class);
        startActivity(appLaunch);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();

        //Show the localized message supplied with the exception or
        //or a default message from the string resources if a
        //localized message cannot be obtained
        String msg;
        if (null == (msg = e.getLocalizedMessage())) {
            msg = getString(R.string.signin_err);
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}