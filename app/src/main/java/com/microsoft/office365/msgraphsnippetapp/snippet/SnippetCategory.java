/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.msgraphapiservices.MSGraphContactService;
import com.microsoft.office365.msgraphapiservices.MSGraphDrivesService;
import com.microsoft.office365.msgraphapiservices.MSGraphEventsService;
import com.microsoft.office365.msgraphapiservices.MSGraphGroupsService;
import com.microsoft.office365.msgraphapiservices.MSGraphMailService;
import com.microsoft.office365.msgraphapiservices.MSGraphMeService;
import com.microsoft.office365.msgraphapiservices.MSGraphUserService;
import com.microsoft.office365.msgraphsnippetapp.application.SnippetApp;

import static com.microsoft.office365.msgraphsnippetapp.R.string.section_contacts;
import static com.microsoft.office365.msgraphsnippetapp.R.string.section_drives;
import static com.microsoft.office365.msgraphsnippetapp.R.string.section_events;
import static com.microsoft.office365.msgraphsnippetapp.R.string.section_groups;
import static com.microsoft.office365.msgraphsnippetapp.R.string.section_me;
import static com.microsoft.office365.msgraphsnippetapp.R.string.section_messages;
import static com.microsoft.office365.msgraphsnippetapp.R.string.section_user;

public class SnippetCategory<T> {
    static final SnippetCategory<MSGraphContactService> contactSnippetCategory
            = new SnippetCategory<>(section_contacts, create(MSGraphContactService.class));

    static final SnippetCategory<MSGraphEventsService> eventsSnippetCategory
            = new SnippetCategory<>(section_events, create(MSGraphEventsService.class));

    static final SnippetCategory<MSGraphGroupsService> groupSnippetCategory
            = new SnippetCategory<>(section_groups, create(MSGraphGroupsService.class));

    static final SnippetCategory<MSGraphUserService> userSnippetCategory
            = new SnippetCategory<>(section_user, create(MSGraphUserService.class));

    static final SnippetCategory<MSGraphMailService> mailSnippetCategory
            = new SnippetCategory<>(section_messages, create(MSGraphMailService.class));

    static final SnippetCategory<MSGraphMeService> meSnippetCategory
            = new SnippetCategory<>(section_me, create(MSGraphMeService.class));

    static final SnippetCategory<MSGraphDrivesService> drivesSnippetCategory
            = new SnippetCategory<>(section_drives, create(MSGraphDrivesService.class));

    final String mSection;
    final T mService;

    SnippetCategory(int sectionId, T service) {
        mSection = SnippetApp.getApp().getString(sectionId);
        mService = service;
    }

    private static <T> T create(Class<T> clazz) {
        return SnippetApp.getApp().getRestAdapter().create(clazz);
    }
}