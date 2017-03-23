/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.json.gson.GsonFactory;
import com.microsoft.office365.msgraphsnippetapp.authentication.AuthenticationCallback;
import com.microsoft.office365.msgraphsnippetapp.authentication.AuthenticationManager;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.microsoft.office365.msgraphsnippetapp.R.id.layout_diagnostics;
import static com.microsoft.office365.msgraphsnippetapp.R.id.o365_signin;
import static com.microsoft.office365.msgraphsnippetapp.R.id.view_diagnosticsdata;
import static com.microsoft.office365.msgraphsnippetapp.R.layout.activity_signin;
import static com.microsoft.office365.msgraphsnippetapp.R.string.warning_clientid_redirecturi_incorrect;

public class SignInActivity
        extends BaseActivity
         {

    @InjectView(layout_diagnostics)
    protected View mDiagnosticsLayout;

    @InjectView(view_diagnosticsdata)
    protected TextView mDiagnosticsTxt;

             private static final String TAG = "SigninActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signin);

        ButterKnife.inject(this);
    }

    @OnClick(o365_signin)
    public void onSignInO365Clicked() {
        try {
            authenticate();
        } catch (IllegalArgumentException e) {
            warnBadClient();
        }
    }


    private void warnBadClient() {
        Toast.makeText(this,
                warning_clientid_redirecturi_incorrect,
                Toast.LENGTH_LONG)
                .show();
    }

    private void authenticate() throws IllegalArgumentException {
        // define the post-auth callback
        com.microsoft.office365.msgraphsnippetapp.authentication.AuthenticationCallback<String, String> oidclibcallback =
                new AuthenticationCallback<String, String>() {

                    @Override
                    public void onSuccess(String idToken, String authToken) {
                        String name = "";
                        String preferredUsername = "";
                        try {
                            // get the user info from the id token
                            IdToken claims = IdToken.parse(new GsonFactory(), idToken);
                            name = claims.getPayload().get("name").toString();
                            preferredUsername = claims.getPayload().get("preferred_username").toString();
                            SharedPrefsUtil.persistAuthToken( authToken);
                            //start the snippets
                            Intent snippetIntent = new Intent(SignInActivity.this,SnippetListActivity.class);
                            startActivity(snippetIntent);

                        } catch (IOException ioe) {
                            Log.e(TAG, ioe.getMessage());
                        } catch (NullPointerException npe) {
                            Log.e(TAG, npe.getMessage());

                        }


                    }

                    @Override
                    public void onError(Exception exc) {

                    }
                };

        AuthenticationManager mgr = AuthenticationManager.getInstance(this);
        mgr.connect(this, oidclibcallback);
    }

    private void validateOrganizationArgs() throws IllegalArgumentException {
        UUID.fromString(ServiceConstants.CLIENT_ID);
        URI.create(ServiceConstants.REDIRECT_URI);
    }

    private void start() {
        Intent appLaunch = new Intent(this, SnippetListActivity.class);
        startActivity(appLaunch);
    }

}
