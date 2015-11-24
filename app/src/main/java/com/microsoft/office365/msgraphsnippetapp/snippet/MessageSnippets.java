/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import android.content.Context;

import com.microsoft.office365.msgraphapiservices.MSGraphMailService;
import com.microsoft.office365.msgraphsnippetapp.R;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;
import com.microsoft.office365.msgraphsnippetapp.inject.AppModule;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import retrofit.Callback;

import static com.microsoft.office365.msgraphsnippetapp.R.array.get_user_messages;
import static com.microsoft.office365.msgraphsnippetapp.R.array.send_an_email_message;

public abstract class MessageSnippets<Result> extends AbstractSnippet<MSGraphMailService, Result> {
    /**
     * Snippet constructor
     *
     * @param descriptionArray The String array for the specified snippet
     */
    public MessageSnippets(Integer descriptionArray) {
        super(SnippetCategory.mailSnippetCategory, descriptionArray);
    }

    static MessageSnippets[] getMessageSnippets() {
        return new MessageSnippets[]{
                // Marker element
                new MessageSnippets(null) {
                    @Override
                    public void request(MSGraphMailService service, Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /* Get messages from mailbox for signed in user
                 * HTTP GET https://graph.microsoft.com/{version}/me/messages
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_list_messages
                 */
                new MessageSnippets<Void>(get_user_messages) {
                    @Override
                    public void request(MSGraphMailService service, Callback<Void> callback) {
                        service.getMail(
                                getVersion(),
                                callback);
                    }
                },

                /* Sends an email message on behalf of the signed in user
                 * HTTP POST https://graph.microsoft.com/{version}/me/messages/sendMail
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_post_messages
                 */
//                new MessageSnippets<Void>(send_an_email_message) {
//                    @Override
//                    public void request(MSGraphMailService service, Callback<Void> callback) {
//                        service.createNewMail(
//                                getVersion(),
//                                createMailPayload(
//                                        SnippetApp.getApp().getString(R.string.mailSubject),
//                                        SnippetApp.getApp().getString(R.string.mailBody),
//                                        SnippetApp.getApp().getSharedPreferences(AppModule.PREFS,
//                                                Context.MODE_PRIVATE).getString(SharedPrefsUtil.PREF_USER_ID, "")),
//                                callback);
//                    }
//                }
        };
    }

    @Override
    public abstract void request(MSGraphMailService service, Callback<Result> callback);

}
