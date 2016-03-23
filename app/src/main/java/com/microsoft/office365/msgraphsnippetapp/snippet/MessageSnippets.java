/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import android.content.SharedPreferences;

import com.microsoft.office365.microsoftgraphvos.EmailAddress;
import com.microsoft.office365.microsoftgraphvos.ItemBody;
import com.microsoft.office365.microsoftgraphvos.Message;
import com.microsoft.office365.microsoftgraphvos.MessageWrapper;
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
                        // Get a context so we can interrogate Resources & SharedPreferences
                        SnippetApp app = SnippetApp.getApp();
                        SharedPreferences prefs = SharedPrefsUtil.getSharedPreferences();

                        // load the contents
                        String subject = app.getString(R.string.mailSubject);
                        String body = app.getString(R.string.mailBody);
                        String recipient = prefs.getString(SharedPrefsUtil.PREF_USER_ID, "");

                        // make it
                        MessageWrapper msgWrapper = createMessage(subject, body, recipient);

                        // send it
                        service.createNewMail(getVersion(), msgWrapper, callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(MSGraphMailService service, Callback<Result> callback);

    private static MessageWrapper createMessage(
            String msgSubject,
            String msgBody,
            String... msgRecipients) {
        Message msg = new Message();

        // add the recipient
        RecipientVO recipient;
        for (int ii = 0; ii < msgRecipients.length; ii++) {
            // if the recipient array does not exist, new one up
            if (null == msg.toRecipients) {
                msg.toRecipients = new RecipientVO[msgRecipients.length];
            }
            // allocate a new recipient
            recipient = new RecipientVO();
            // give them an email address
            recipient.emailAddress = new EmailAddress();
            // set that address to be the currently iterated-upon recipient string
            recipient.emailAddress.address = msgRecipients[ii];
            // add it to the array at the position
            msg.toRecipients[ii] = recipient;
        }

        // set the subject
        msg.subject = msgSubject;

        // create the body
        ItemBody body = new ItemBody();
        body.contentType = ItemBody.CONTENT_TYPE_TEXT;
        body.content = msgBody;
        msg.body = body;

        MessageWrapper wrapper = new MessageWrapper();
        wrapper.message = msg;
        wrapper.saveToSentItems = true;
        return wrapper;
    }
}
