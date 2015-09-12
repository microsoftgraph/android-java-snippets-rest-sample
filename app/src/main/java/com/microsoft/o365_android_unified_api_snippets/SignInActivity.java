/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.o365_android_unified_api_snippets.util.SharedPrefsUtil;

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

        //I don't think it is a good idea for us to try to connect
        //the user on create. Better to wait until user clicks the connect button
       //     mAuthenticationManager.connect(this);

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
// *********************************************************
//
// O365-Android-Unified-API-Snippets, https://github.com/OfficeDev/O365-Android-Unified-API-Snippets
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// *********************************************************
