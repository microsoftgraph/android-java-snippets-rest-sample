/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import android.content.SharedPreferences;

import com.microsoft.office365.microsoftgraphvos.PasswordProfile;
import com.microsoft.office365.microsoftgraphvos.User;
import com.microsoft.office365.msgraphapiservices.MSGraphUserService;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import java.util.UUID;

import retrofit.Callback;
import retrofit.client.Response;

import static com.microsoft.office365.msgraphsnippetapp.R.array.get_organization_filtered_users;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_organization_users;
import static com.microsoft.office365.msgraphsnippetapp.R.array.insert_organization_user;
import static com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil.PREF_USER_TENANT;

public abstract class UsersSnippets<Result> extends AbstractSnippet<MSGraphUserService, Result> {

    public UsersSnippets(Integer descriptionArray) {
        super(SnippetCategory.userSnippetCategory, descriptionArray);
    }

    static UsersSnippets[] getUsersSnippets() {
        return new UsersSnippets[]{
                // Marker element
                new UsersSnippets(null) {

                    @Override
                    public void request(MSGraphUserService msGraphUserService, Callback callback) {
                    }
                },

                /*
                 * Gets all of the users in your tenant\'s directory.
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/users
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_list
                 */
                new UsersSnippets<Response>(get_organization_users) {
                    @Override
                    public void request(
                            MSGraphUserService msGraphUserService,
                            Callback<Response> callback) {
                        msGraphUserService.getUsers(getVersion(), callback);
                    }
                },

                /*
                 * Gets all of the users in your tenant's directory who are from the United States, using $filter.
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/users?$filter=country eq \'United States\'
                 * @see http://graph.microsoft.io/docs/overview/query_parameters
                 */
                new UsersSnippets<Response>(get_organization_filtered_users) {
                    @Override
                    public void request(
                            MSGraphUserService msGraphUserService,
                            Callback<Response> callback) {
                        msGraphUserService.getFilteredUsers(
                                getVersion(),
                                "country eq 'United States'",
                                callback);
                    }
                },

                 /*
                 * Adds a new user to the tenant's directory
                 * HTTP POST https://graph.microsoft.com/{version}/myOrganization/users
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_post_users
                 */
                new UsersSnippets<Response>(insert_organization_user) {
                    @Override
                    public void request(
                            MSGraphUserService msGraphUserService,
                            Callback<Response> callback) {
                        //Use a random UUID for the user name
                        String randomUserName = UUID.randomUUID().toString();

                        // create the user
                        User user = new User();
                        user.accountEnabled = true;
                        user.displayName = "SAMPLE " + randomUserName;
                        user.mailNickname = randomUserName;

                        // get the tenant from preferences
                        SharedPreferences prefs = SharedPrefsUtil.getSharedPreferences();
                        String tenant = prefs.getString(PREF_USER_TENANT, "");
                        user.userPrincipalName = randomUserName + "@" + tenant;

                        // initialize a password & say whether or not the user must change it
                        PasswordProfile password = new PasswordProfile();
                        password.password = UUID.randomUUID().toString().substring(0, 16);
                        password.forceChangePasswordNextSignIn = false;

                        user.passwordProfile = password;

                        msGraphUserService.createNewUser(getVersion(), user, callback);
                    }
                }
        };
    }

    public abstract void request(MSGraphUserService msGraphUserService, Callback<Result> callback);
}