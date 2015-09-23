/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.o365_android_unified_api_snippets.R;
import com.microsoft.o365_android_unified_api_snippets.application.SnippetApp;
import com.microsoft.o365_android_unified_api_snippets.inject.AppModule;
import com.microsoft.o365_android_unified_api_snippets.util.SharedPrefsUtil;
import com.microsoft.unifiedapi.service.UnifiedMailService;

import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.get_user_messages;
import static com.microsoft.o365_android_unified_api_snippets.R.array.send_an_email_message;

public abstract class MessageSnippets<Result> extends AbstractSnippet<UnifiedMailService, Result> {
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
                    public void request(UnifiedMailService service, retrofit.Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /* Get messages from mailbox for signed in user
                 * HTTP GET https://graph.microsoft.com/{version}/me/messages
                 */
                new MessageSnippets<Void>(get_user_messages) {
                    @Override
                    public void request(UnifiedMailService service, retrofit.Callback<Void> callback) {
                        service.getMail(
                                getVersion(),
                                callback);
                    }
                },

                /* Sends an email message on behalf of the signed in user
                 * HTTP POST https://graph.microsoft.com/{version}/me/messages/sendMail
                 */
                new MessageSnippets<Void>(send_an_email_message) {
                    @Override
                    public void request(UnifiedMailService service, retrofit.Callback<Void> callback) {
                        service.postNewMail(
                                getVersion(),
                                createMailPayload(
                                        SnippetApp.getApp().getString(R.string.mailSubject),
                                        SnippetApp.getApp().getString(R.string.mailBody),
                                        SnippetApp.getApp().getSharedPreferences(AppModule.PREFS,
                                                Context.MODE_PRIVATE).getString(SharedPrefsUtil.PREF_USER_ID, "")),
                                callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(UnifiedMailService service, retrofit.Callback<Result> callback);

    protected TypedString createMailPayload(
            String subject,
            String body,
            String address) {
        JsonObject jsonObject_Body = new JsonObject();
        jsonObject_Body.addProperty("ContentType", "Text");
        jsonObject_Body.addProperty("Content", body);

        JsonObject jsonObject_ToAddress = new JsonObject();
        jsonObject_ToAddress.addProperty("Address", address);

        JsonObject jsonObject_ToRecipient = new JsonObject();
        jsonObject_ToRecipient.add("EmailAddress", jsonObject_ToAddress);

        JsonArray toRecipients = new JsonArray();
        toRecipients.add(jsonObject_ToRecipient);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Subject", subject);
        jsonObject.add("Body", jsonObject_Body);
        jsonObject.add("ToRecipients", toRecipients);

        JsonObject messageObject = new JsonObject();
        messageObject.add("Message", jsonObject);
        messageObject.addProperty("SaveToSentItems", true);
        return new TypedString(messageObject.toString()) {
            @Override
            public String mimeType() {
                return "application/json";
            }
        };
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