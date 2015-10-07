/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedsnippetapp.snippet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.microsoft.office365.unifiedsnippetapp.snippet.ContactsSnippets.getContactsSnippets;
import static com.microsoft.office365.unifiedsnippetapp.snippet.DrivesSnippets.getDrivesSnippets;
import static com.microsoft.office365.unifiedsnippetapp.snippet.EventsSnippets.getEventsSnippets;
import static com.microsoft.office365.unifiedsnippetapp.snippet.GroupsSnippets.getGroupsSnippets;
import static com.microsoft.office365.unifiedsnippetapp.snippet.MeSnippets.getMeSnippets;
import static com.microsoft.office365.unifiedsnippetapp.snippet.MessageSnippets.getMessageSnippets;
import static com.microsoft.office365.unifiedsnippetapp.snippet.UsersSnippets.getUsersSnippets;

public class SnippetContent {


    public static final List<AbstractSnippet<?, ?>> ITEMS = new ArrayList<>();

    static {
        AbstractSnippet<?, ?>[][] baseSnippets = new AbstractSnippet<?, ?>[][]{
                getContactsSnippets(),
                getGroupsSnippets(),
                getEventsSnippets(),
                getMeSnippets(),
                getMessageSnippets(),
                getUsersSnippets(),
                getDrivesSnippets()
        };

        for (AbstractSnippet<?, ?>[] snippetArray : baseSnippets) {
            Collections.addAll(ITEMS, snippetArray);
        }
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