/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.microsoft.unifiedapi.service.UnifiedEventsService;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.create_event;
import static com.microsoft.o365_android_unified_api_snippets.R.array.delete_event;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_user_events;
import static com.microsoft.o365_android_unified_api_snippets.R.array.update_event;

public abstract class EventsSnippets<Result> extends AbstractSnippet<UnifiedEventsService, Result> {

    public EventsSnippets(Integer descriptionArray) {
        super(SnippetCategory.eventsSnippetCategory, descriptionArray);
    }

    static EventsSnippets[] getEventsSnippets() {
        return new EventsSnippets[]{
                // Marker element
                new EventsSnippets(null) {

                    @Override
                    public void request(UnifiedEventsService o, Callback callback) {
                        //No implementation
                    }
                },
                //Snippets

                /*
                 * Get all events for the signed in user.
                 * HTTP GET https://graph.microsoft.com/{version}/me/events
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new EventsSnippets<Void>(get_user_events) {

                    @Override
                    public void request(
                            UnifiedEventsService unifiedEventsService,
                            retrofit.Callback<Void> callback) {
                        unifiedEventsService.getEvents(getVersion(), callback);
                    }
                },

                /*
                 * Adds an event to the signed-in user\'s calendar.
                 * HTTP POST https://graph.microsoft.com/{version}/me/events
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new EventsSnippets<Void>(create_event) {

                    @Override
                    public void request(
                            UnifiedEventsService unifiedEventsService,
                            retrofit.Callback<Void> callback) {

                        JsonObject newEvent = createNewEventJsonBody();

                        TypedString body = new TypedString(newEvent.toString()) {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };

                        //Call service to POST the new event
                        unifiedEventsService.postNewEvent(getVersion(), body, callback);
                    }

                },
                 /*
                 * Update an event
                 * HTTP PATCH https://graph.microsoft.com/{version}/me/events/{Event.Id}
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new EventsSnippets<Void>(update_event) {

                    @Override
                    public void request(
                            final UnifiedEventsService unifiedEventsService,
                            retrofit.Callback<Void> callback) {
                        final PlaceToStash stash = new PlaceToStash();
                        final JsonObject newEvent = createNewEventJsonBody();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                TypedString body = new TypedString(newEvent.toString()) {
                                    @Override
                                    public String mimeType() {
                                        return "application/json";
                                    }
                                };
                                //insert an event that we will update later
                                retrofit.client.Response responseNewEvent = unifiedEventsService.postNewEventSynchronous(
                                        getVersion(),
                                        body);
                                stash.resp = responseNewEvent;
                            }
                        };
                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getGroupId(stash.resp);

                            //update the group we created
                            JsonObject updateEvent = newEvent;
                            updateEvent.remove("Subject");
                            updateEvent.addProperty("Subject", "Sync of the Week");

                            TypedString updateBody = new TypedString(updateEvent.toString()) {
                                @Override
                                public String mimeType() {
                                    return "application/json";
                                }
                            };
                            unifiedEventsService.patchEvent(
                                    getVersion(),
                                    groupID,
                                    updateBody,
                                    callback);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                 /*
                 * Delete an event
                 * HTTP DELETE https://graph.microsoft.com/{version}/me/events/{Event.Id}
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new EventsSnippets<Void>(delete_event) {

                    @Override
                    public void request(
                            final UnifiedEventsService unifiedEventsService,
                            retrofit.Callback<Void> callback) {
                        final PlaceToStash stash = new PlaceToStash();
                        final JsonObject newEvent = createNewEventJsonBody();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                TypedString body = new TypedString(newEvent.toString()) {
                                    @Override
                                    public String mimeType() {
                                        return "application/json";
                                    }
                                };
                                //insert an event that we will delete later
                                retrofit.client.Response responseNewEvent = unifiedEventsService.postNewEventSynchronous(
                                        getVersion(),
                                        body);
                                stash.resp = responseNewEvent;
                            }
                        };
                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getGroupId(stash.resp);

                            //Delete the group we created
                            unifiedEventsService.deleteEvent(
                                    getVersion(),
                                    groupID,
                                    callback);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

        };
    }

    @NonNull
    private static JsonObject createNewEventJsonBody() {
        //Set start time to now and end in 1 hour
        DateTime start = new DateTime().now();
        DateTime end = start.plusHours(1);

        //create body
        JsonObject newEvent = new JsonObject();
        newEvent.addProperty("Subject", "Office 365 unified API discussion");
        newEvent.addProperty("Start", start.toString());
        newEvent.addProperty("End", end.toString());

        //create location
        JsonObject location = new JsonObject();
        location.addProperty("DisplayName", "Bill's office");
        newEvent.add("Location", location);

        //create attendees array with one attendee
        //start with attendee
        JsonObject attendee = new JsonObject();
        attendee.addProperty("Type", "Required");
        JsonObject emailaddress = new JsonObject();
        emailaddress.addProperty("Address", "mara@fabrikam.com");
        attendee.add("EmailAddress", emailaddress);

        //now create attendees array
        JsonArray attendees = new JsonArray();
        attendees.add(attendee);
        newEvent.add("Attendees", attendees);

        //create email body
        JsonObject emailBody = new JsonObject();
        emailBody.addProperty("Content", "Let's discuss the power of the Office 365 unified API.");
        emailBody.addProperty("ContentType", "Text");
        newEvent.add("Body", emailBody);
        return newEvent;
    }

    public abstract void request(UnifiedEventsService unifiedEventsService, Callback<Result> callback);

    /**
     * Gets the group object id from the HTTP response object
     * returned from a group REST call. Method expects that the JSON is a single
     * group object.
     *
     * @param json The JSON to parse. Expected to be a single group object
     * @return The group id (objectID) of the first group found in the array.
     */
    protected String getGroupId(retrofit.client.Response json) {
        if (json == null)
            return "";

        String groupID;

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(json.getBody().in(), "UTF-8"));
            JsonElement responseElement = new JsonParser().parse(reader);
            JsonObject responseObject = responseElement.getAsJsonObject();
            groupID = responseObject.get("Id").getAsString();
            return groupID;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    class PlaceToStash {
        public retrofit.client.Response resp;
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
