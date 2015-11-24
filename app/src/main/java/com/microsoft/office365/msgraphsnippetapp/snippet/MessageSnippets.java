/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import android.content.SharedPreferences;

import com.microsoft.office365.microsoftgraphvos.EmailAddressVO;
import com.microsoft.office365.microsoftgraphvos.ItemBodyVO;
import com.microsoft.office365.microsoftgraphvos.MessageVO;
import com.microsoft.office365.microsoftgraphvos.MessageWrapperVO;
import com.microsoft.office365.microsoftgraphvos.RecipientVO;
import com.microsoft.office365.msgraphapiservices.MSGraphMailService;
import com.microsoft.office365.msgraphsnippetapp.R;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;
import com.microsoft.office365.msgraphsnippetapp.util.SharedPrefsUtil;

import retrofit.Callback;
import retrofit.client.Response;

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
                new MessageSnippets<Response>(get_user_messages) {
                    @Override
                    public void request(MSGraphMailService service, Callback<Response> callback) {
                        service.getMail(
                                getVersion(),
                                callback);
                    }
                },

                /* Sends an email message on behalf of the signed in user
                 * HTTP POST https://graph.microsoft.com/{version}/me/messages/sendMail
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_post_messages
                 */
                new MessageSnippets<Response>(send_an_email_message) {
                    @Override
                    public void request(MSGraphMailService service, Callback<Response> callback) {
                        SnippetApp app = SnippetApp.getApp();
                        SharedPreferences prefs = SharedPrefsUtil.getSharedPreferences();

                        // load the contents
                        String subject = app.getString(R.string.mailSubject);
                        String body = app.getString(R.string.mailBody);
                        String recipient = prefs.getString(SharedPrefsUtil.PREF_USER_ID, "");

                        // make it
                        MessageWrapperVO msgWrapper = createMessage(subject, body, recipient);

                        // send it
                        service.createNewMail(getVersion(), msgWrapper, callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(MSGraphMailService service, Callback<Result> callback);

    private static MessageWrapperVO createMessage(
            String msgSubject,
            String msgBody,
            String msgRecipient) {
        MessageVO msg = new MessageVO();

        // add the recipient
        RecipientVO recipient = new RecipientVO();
        recipient.emailAddress = new EmailAddressVO();
        recipient.emailAddress.address = msgRecipient;
        msg.toRecipients = new RecipientVO[]{recipient};

        // set the subject
        msg.subject = msgSubject;

        // create the body
        ItemBodyVO body = new ItemBodyVO();
        body.contentType = ItemBodyVO.CONTENT_TYPE_TEXT;
        body.content = msgBody;
        msg.body = body;

        MessageWrapperVO wrapper = new MessageWrapperVO();
        wrapper.message = msg;
        wrapper.saveToSentItems = true;
        return wrapper;
    }
}
