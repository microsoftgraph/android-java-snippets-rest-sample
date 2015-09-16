/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.o365_android_unified_api_snippets.R;
import com.microsoft.o365_android_unified_api_snippets.application.SnippetApp;
import com.microsoft.o365_android_unified_api_snippets.inject.AppModule;
import com.microsoft.o365_android_unified_api_snippets.util.SharedPrefsUtil;
import com.microsoft.unifiedapi.service.UnifiedContactService;
import com.microsoft.unifiedvos.ContactVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import retrofit.Callback;
import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.get_all_contacts;
import static com.microsoft.o365_android_unified_api_snippets.R.array.insert_a_contact;

public abstract class ContactsSnippets<Result> extends AbstractSnippet<UnifiedContactService, Result> {

    public ContactsSnippets(Integer descriptionArray) {
        super(SnippetCategory.contactSnippetCategory, descriptionArray);
    }


    static ContactsSnippets[] getContactsSnippets() {
        return new ContactsSnippets[]{
                // Marker element
                new ContactsSnippets(null) {
                    @Override
                    public void request(UnifiedContactService service, Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /**
                 * Gets all of the user's contacts
                 */
                new ContactsSnippets<Void>(get_all_contacts) {
                    @Override
                    public void request(UnifiedContactService service, Callback<Void> callback) {
                        service.getContacts(
                                getVersion(),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Inserts a contact into the organization
                 */
                new ContactsSnippets<Void>(insert_a_contact) {
                    @Override
                    public void request(UnifiedContactService service, Callback<Void> callback) {
                        String tenant = SnippetApp
                                .getApp()
                                .getSharedPreferences(
                                        AppModule.PREFS,
                                        Context.MODE_PRIVATE)
                                .getString(SharedPrefsUtil.PREF_USER_TENANT,"");

                        TypedString bla = createNewContact(tenant);
                        service.insertContact(
                                getVersion(),
                                bla,
                                callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(UnifiedContactService service, Callback<Result> callback);

    /**
     * Creates a Json payload for a POST operation to
     * insert a new group
     * @return TypedString. The Json body
     */
    protected TypedString createNewContact(String tenant) {


        String randomName = UUID.randomUUID().toString();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SnippetApp.getApp().getString(R.string.userPrincipalName),
                "Contact: " + randomName + '@' + tenant);
        jsonObject.addProperty("displayName", "Contact: " + randomName);
        jsonObject.addProperty("accountEnabled", false);
        jsonObject.addProperty("mailNickname", UUID.randomUUID().toString());
        jsonObject.addProperty("securityEnabled", true);
        return new TypedString(jsonObject.toString()) {
            @Override
            public String mimeType() {
                return "application/json";
            }
        };
    }

    /**
     * Creates a Json object for the body of a PATCH operation
     * @return
     */
    protected  TypedString createUpdateBody() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", UUID.randomUUID().toString());
        jsonObject.addProperty("mailEnabled", false);
        jsonObject.addProperty("mailNickname", UUID.randomUUID().toString());
        jsonObject.addProperty("securityEnabled", true);
        return new TypedString(jsonObject.toString()) {
            @Override
            public String mimeType() {
                return "application/json";
            }
        };
    }

    /**
     * Gets the directory object id from the HTTP response object
     * returned from a group REST call
     * @param json
     * @return String object id
     */
    protected String getObjectId(retrofit.client.Response json)
    {
        if (json == null)
            return "";

        String contactId = null;
        try {
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(
                            json.getBody().in()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Gson gson = new Gson();
            ContactVO contact = gson.fromJson(
                    total.toString(),
                    ContactVO.class);

            contactId = contact.objectId;
        }
        catch (IOException ex){}
        return contactId;
    }

    class PlaceToStash {
        public retrofit.client.Response resp;
        public IOException wentWrong;
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