/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedsnippetapp.snippet;

import com.google.gson.JsonObject;
import com.microsoft.office365.unifiedsnippetapp.services.UnifiedUserService;
import com.microsoft.office365.unifiedsnippetapp.util.SharedPrefsUtil;

import java.util.UUID;

import retrofit.mime.TypedString;

import static com.microsoft.office365.unifiedsnippetapp.R.array.get_organization_filtered_users;
import static com.microsoft.office365.unifiedsnippetapp.R.array.get_organization_users;
import static com.microsoft.office365.unifiedsnippetapp.R.array.insert_organization_user;


public abstract class UsersSnippets<Result> extends AbstractSnippet<UnifiedUserService, Result> {

    public UsersSnippets(Integer descriptionArray) {
        super(SnippetCategory.userSnippetCategory, descriptionArray);
    }

    static UsersSnippets[] getUsersSnippets() {
        return new UsersSnippets[]{
                // Marker element
                new UsersSnippets(null) {

                    @Override
                    public void request(UnifiedUserService o, retrofit.Callback callback) {
                    }
                },

                /*
                 * Gets all of the users in your tenant\'s directory.
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/users
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_User
                 */
                new UsersSnippets<Void>(get_organization_users) {
                    @Override
                    public void request(
                            UnifiedUserService unifiedUserService,
                            retrofit.Callback<Void> callback) {
                        unifiedUserService.getUsers(getVersion(), callback);
                    }
                },

                /*
                 * Gets all of the users in your tenant's directory who are from the United States, using $filter.
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/users?$filter=country eq \'United States\'
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_User
                 */
                new UsersSnippets<Void>(get_organization_filtered_users) {
                    @Override
                    public void request(
                            UnifiedUserService unifiedUserService,
                            retrofit.Callback<Void> callback) {
                        unifiedUserService.getFilteredUsers(getVersion(), "country eq 'United States'", callback);
                    }
                },

                 /*
                 * Adds a new user to the tenant's directory
                 * HTTP POST https://graph.microsoft.com/{version}/myOrganization/users
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_User
                 */
                new UsersSnippets<Void>(insert_organization_user) {
                    @Override
                    public void request(
                            UnifiedUserService unifiedUserService,
                            retrofit.Callback<Void> callback) {

                        //Use a random UUI for the user name
                        String randomUserName = UUID.randomUUID().toString();

                        //create body
                        JsonObject newUser = new JsonObject();
                        newUser.addProperty("accountEnabled", true);
                        newUser.addProperty("displayName", randomUserName);
                        newUser.addProperty("mailNickname", randomUserName);
                        String tenant = SharedPrefsUtil.getSharedPreferences().getString(SharedPrefsUtil.PREF_USER_TENANT, "");
                        newUser.addProperty("userPrincipalName", randomUserName + '@' + tenant);

                        //create password profile
                        JsonObject passwordProfile = new JsonObject();
                        passwordProfile.addProperty("password", "p@ssw0rd!");
                        passwordProfile.addProperty("forceChangePasswordNextLogin", false);
                        newUser.add("passwordProfile", passwordProfile);

                        TypedString body = new TypedString(newUser.toString()) {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };

                        //Call service to POST the new user
                        unifiedUserService.postNewUser(getVersion(), body, callback);
                    }
                }
        };
    }

    public abstract void request(UnifiedUserService unifiedUserService, retrofit.Callback<Result> callback);
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