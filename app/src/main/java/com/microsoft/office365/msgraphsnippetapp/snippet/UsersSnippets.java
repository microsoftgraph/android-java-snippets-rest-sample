/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.google.gson.JsonObject;
import com.microsoft.office365.msgraphapiservices.MSGraphUserService;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import java.util.UUID;

import retrofit.Callback;
import retrofit.mime.TypedString;

import static com.microsoft.office365.msgraphsnippetapp.R.array.get_organization_filtered_users;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_organization_users;
import static com.microsoft.office365.msgraphsnippetapp.R.array.insert_organization_user;

public abstract class UsersSnippets<Result> extends AbstractSnippet<MSGraphUserService, Result> {

    public UsersSnippets(Integer descriptionArray) {
        super(SnippetCategory.userSnippetCategory, descriptionArray);
    }

    static UsersSnippets[] getUsersSnippets() {
        return new UsersSnippets[]{
                // Marker element
                new UsersSnippets(null) {

                    @Override
                    public void request(MSGraphUserService o, Callback callback) {
                    }
                },

                /*
                 * Gets all of the users in your tenant\'s directory.
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/users
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_list
                 */
                new UsersSnippets<Void>(get_organization_users) {
                    @Override
                    public void request(
                            MSGraphUserService MSGraphUserService,
                            Callback<Void> callback) {
                        MSGraphUserService.getUsers(getVersion(), callback);
                    }
                },

                /*
                 * Gets all of the users in your tenant's directory who are from the United States, using $filter.
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/users?$filter=country eq \'United States\'
                 * @see http://graph.microsoft.io/docs/overview/query_parameters
                 */
                new UsersSnippets<Void>(get_organization_filtered_users) {
                    @Override
                    public void request(
                            MSGraphUserService MSGraphUserService,
                            Callback<Void> callback) {
                        MSGraphUserService.getFilteredUsers(getVersion(), "country eq 'United States'", callback);
                    }
                },

                 /*
                 * Adds a new user to the tenant's directory
                 * HTTP POST https://graph.microsoft.com/{version}/myOrganization/users
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_post_users
                 */
                new UsersSnippets<Void>(insert_organization_user) {
                    @Override
                    public void request(
                            MSGraphUserService MSGraphUserService,
                            Callback<Void> callback) {

                        //Use a random UUI for the user name
                        String randomUserName = UUID.randomUUID().toString();

                        //create body
                        JsonObject newUser = new JsonObject();
                        newUser.addProperty("accountEnabled", true);
                        newUser.addProperty("displayName", "SAMPLE " + randomUserName);
                        newUser.addProperty("mailNickname", randomUserName);
                        String tenant = SharedPrefsUtil.getSharedPreferences().getString(SharedPrefsUtil.PREF_USER_TENANT, "");
                        newUser.addProperty("userPrincipalName", randomUserName + '@' + tenant);

                        //create password profile
                        JsonObject passwordProfile = new JsonObject();

                        //Generate random UUID for the password which has 16 character maximum
                        passwordProfile.addProperty("password", UUID.randomUUID().toString().substring(0, 16));
                        passwordProfile.addProperty("forceChangePasswordNextLogin", false);
                        newUser.add("passwordProfile", passwordProfile);

                        TypedString body = new TypedString(newUser.toString()) {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };

                        //Call service to POST the new user
                        MSGraphUserService.createNewUser(getVersion(), body, callback);
                    }
                }
        };
    }

    public abstract void request(MSGraphUserService MSGraphUserService, Callback<Result> callback);
}