/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.unifiedapi.service.UnifiedEventsService;

import org.joda.time.DateTime;

import retrofit.Callback;
import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.create_event;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_user_events;


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
                 * GET all events for the signed in user.
                 * HTTP GET https://graph.microsoft.com/beta/me/events
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
                 * POST Adds an event to the signed-in user\'s calendar.
                 * HTTP POST https://graph.microsoft.com/beta/me/events
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new EventsSnippets<Void>(create_event) {

                    @Override
                    public void request(
                            UnifiedEventsService unifiedEventsService,
                            retrofit.Callback<Void> callback) {
                        //Create body defining the new event
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

                        TypedString body = new TypedString(newEvent.toString()) {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };

                        //Call service to POST the new event
                        unifiedEventsService.postNewEvent(getVersion(), body, callback);
                    }

                }

        };
    }

    public abstract void request(UnifiedEventsService unifiedEventsService, Callback<Result> callback);
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
