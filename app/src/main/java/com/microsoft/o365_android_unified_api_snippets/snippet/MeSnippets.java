/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;


import com.microsoft.o365_android_unified_api_snippets.R;
import com.microsoft.o365_android_unified_api_snippets.application.SnippetApp;
import com.microsoft.unifiedapi.service.UnifiedMeService;
import com.microsoft.unifiedapi.service.UnifiedUserService;

import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me_direct_reports;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me_group_membership;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me_manager;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me_photo;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me_responsibilities;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_me_drive;

public abstract class MeSnippets<Result> extends AbstractSnippet<UnifiedMeService, Result> {
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
                    public void request(UnifiedMeService service, retrofit.Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /**
                 * Gets me (signed in user)
                 */
                new MeSnippets<Void>(get_me) {
                    @Override
                    public void request(UnifiedMeService service, retrofit.Callback<Void> callback) {
                        service.getMe(
                                getVersion(),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Gets my responsibilities
                 */
                new MeSnippets<Void>(get_me_responsibilities) {
                    @Override
                    public void request(UnifiedMeService service, retrofit.Callback<Void> callback) {
                        service.getMeResponsibilities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.meResposibility),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Gets a user's manager
                 */
                new MeSnippets<Void>(get_me_manager) {
                    @Override
                    public void request(UnifiedMeService service, retrofit.Callback<Void> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.manager),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Gets a user's direct reports
                 */
                new MeSnippets<Void>(get_me_direct_reports) {
                    @Override
                    public void request(UnifiedMeService service, retrofit.Callback<Void> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.directReports),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Gets the group membership of a user
                 */
                new MeSnippets<Void>(get_me_group_membership) {
                    @Override
                    public void request(UnifiedMeService service, retrofit.Callback<Void> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.memberOf),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Gets the photo of a user
                 */
                new MeSnippets<Void>(get_me_photo) {
                    @Override
                    public void request(UnifiedMeService service, retrofit.Callback<Void> callback) {
                        service.getMeEntities(
                                getVersion(),
                                SnippetApp.getApp().getString(R.string.userPhoto),
                                callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(UnifiedMeService service, retrofit.Callback<Result> callback);


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