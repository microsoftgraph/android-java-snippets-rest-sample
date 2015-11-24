/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.msgraphapiservices.MSGraphMeService;
import com.microsoft.office365.msgraphsnippetapp.R;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;

import retrofit.Callback;
import retrofit.client.Response;

import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_direct_reports;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_group_membership;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_manager;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_photo;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_responsibilities;

public abstract class MeSnippets<Result> extends AbstractSnippet<MSGraphMeService, Result> {
    /**
     * Snippet constructor
     *
     * @param descriptionArray The String array for the specified snippet
     */
    public MeSnippets(Integer descriptionArray) {
        super(SnippetCategory.meSnippetCategory, descriptionArray);
    }

    static MeSnippets[] getMeSnippets() {
        return new MeSnippets[]{
                // Marker element
                new MeSnippets(null) {
                    @Override
                    public void request(MSGraphMeService service, Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /* Get information about signed in user
                 * HTTP GET https://graph.microsoft.com/{version}/me
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_get
                 */
                new MeSnippets<Response>(get_me) {
                    @Override
                    public void request(MSGraphMeService service, Callback<Response> callback) {
                        service.getMe(
                                getVersion(),
                                callback);
                    }
                },

                /* Get responsibilities of signed in user
                 * HTTP GET https://graph.microsoft.com/{version}/me?$select=AboutMe,Responsibilities,Tags
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/user
                 */
                new MeSnippets<Response>(get_me_responsibilities) {
                    @Override
                    public void request(MSGraphMeService service, Callback<Response> callback) {
                        service.getMeResponsibilities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.meResponsibility),
                                callback);
                    }
                },

                /* Get the user's manager
                 * HTTP GET https://graph.microsoft.com/{version}/me/manager
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/user
                 */
                new MeSnippets<Response>(get_me_manager) {
                    @Override
                    public void request(MSGraphMeService service, Callback<Response> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.manager),
                                callback);
                    }
                },

                /* Get the user's direct reports
                 * HTTP GET https://graph.microsoft.com/{version}/me/directReports
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/user
                 */
                new MeSnippets<Response>(get_me_direct_reports) {
                    @Override
                    public void request(MSGraphMeService service, Callback<Response> callback) {
                        service.getMeEntities(getVersion(),
                                SnippetApp.getApp().getString(R.string.directReports),
                                callback);
                    }
                },

                /* Get the group membership of the user
                 * HTTP GET https://graph.microsoft.com/{version}/me/memberOf
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/user
                 */
                new MeSnippets<Response>(get_me_group_membership) {
                    @Override
                    public void request(MSGraphMeService service, Callback<Response> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.memberOf),
                                callback);
                    }
                },

                /* Get the photo of the user
                 * HTTP GET https://graph.microsoft.com/{version}/me/userPhoto
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/user
                 */
                new MeSnippets<Response>(get_me_photo) {
                    @Override
                    public void request(MSGraphMeService service, Callback<Response> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.userPhoto),
                                callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(MSGraphMeService service, Callback<Result> callback);

}
