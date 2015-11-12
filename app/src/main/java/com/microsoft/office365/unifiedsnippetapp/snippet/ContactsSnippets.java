/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedsnippetapp.snippet;


import com.microsoft.office365.unifiedapiservices.MSGraphContactService;

import retrofit.Callback;

import static com.microsoft.office365.unifiedsnippetapp.R.array.get_all_contacts;

;

public abstract class ContactsSnippets<Result> extends AbstractSnippet<MSGraphContactService, Result> {

    public ContactsSnippets(Integer descriptionArray) {
        super(SnippetCategory.contactSnippetCategory, descriptionArray);
    }


    static ContactsSnippets[] getContactsSnippets() {
        return new ContactsSnippets[]{
                // Marker element
                new ContactsSnippets(null) {
                    @Override
                    public void request(MSGraphContactService service, Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                 /* Get all of the user's contacts
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/contacts
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_contacts
                 */
                new ContactsSnippets<Void>(get_all_contacts) {
                    @Override
                    public void request(MSGraphContactService service, Callback<Void> callback) {
                        service.getContacts(
                                getVersion(),
                                callback);
                    }
                }
        };
    }

    @Override
    public abstract void request(MSGraphContactService service, Callback<Result> callback);

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