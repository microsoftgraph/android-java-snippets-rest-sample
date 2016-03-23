/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.microsoftgraphvos.Attendee;
import com.microsoft.office365.microsoftgraphvos.DateTimeTimeZone;
import com.microsoft.office365.microsoftgraphvos.EmailAddress;
import com.microsoft.office365.microsoftgraphvos.Envelope;
import com.microsoft.office365.microsoftgraphvos.Event;
import com.microsoft.office365.microsoftgraphvos.ItemBody;
import com.microsoft.office365.microsoftgraphvos.Location;
import com.microsoft.office365.msgraphapiservices.MSGraphEventsService;

import org.joda.time.DateTime;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.microsoft.office365.msgraphsnippetapp.R.array.create_event;
import static com.microsoft.office365.msgraphsnippetapp.R.array.delete_event;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_user_events;
import static com.microsoft.office365.msgraphsnippetapp.R.array.update_event;

public abstract class EventsSnippets<Result> extends AbstractSnippet<MSGraphEventsService, Result> {

    public EventsSnippets(Integer descriptionArray) {
        super(SnippetCategory.eventsSnippetCategory, descriptionArray);
    }

    static EventsSnippets[] getEventsSnippets() {
        return new EventsSnippets[]{
                // Marker element
                new EventsSnippets(null) {

                    @Override
                    public void request(MSGraphEventsService o, Callback callback) {
                        //No implementation
                    }
                },
                //Snippets

                /*
                 * Get all events for the signed in user.
                 * GET https://graph.microsoft.com/{version}/me/events
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_list_events
                 */
                new EventsSnippets<Envelope<Event>>(get_user_events) {

                    @Override
                    public void request(
                            MSGraphEventsService MSGraphEventsService,
                            Callback<Envelope<Event>> callback) {
                        MSGraphEventsService.getEvents(getVersion(), callback);
                    }
                },

                /*
                 * Adds an event to the signed-in user\'s calendar.
                 * POST https://graph.microsoft.com/{version}/me/events
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_post_events
                 */
                new EventsSnippets<Event>(create_event) {

                    @Override
                    public void request(
                            MSGraphEventsService MSGraphEventsService,
                            Callback<Event> callback) {
                        MSGraphEventsService.createNewEvent(getVersion(), createEvent(), callback);
                    }

                },
                 /*
                 * Update an event
                 * PATCH https://graph.microsoft.com/{version}/me/events/{Event.Id}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/event_update
                 */
                new EventsSnippets<Event>(update_event) {

                    @Override
                    public void request(
                            final MSGraphEventsService MSGraphEventsService,
                            final Callback<Event> callback) {
                        // create a new event to update
                        MSGraphEventsService.createNewEvent(
                                getVersion(),
                                createEvent(),
                                new Callback<Event>() {
                                    @Override
                                    public void success(Event eventVO, Response response) {
                                        // now that the event has been created,
                                        // let's change the subject
                                        Event amended = new Event();
                                        amended.subject = "Weekly Sync Meeting";

                                        MSGraphEventsService.updateEvent(
                                                getVersion(),
                                                eventVO.id,
                                                amended,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        callback.failure(error);
                                    }
                                });
                    }

                },
                 /*
                 * Delete an event
                 * DELETE https://graph.microsoft.com/{version}/me/events/{Event.Id}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/event_delete
                 */
                new EventsSnippets<Response>(delete_event) {

                    @Override
                    public void request(
                            final MSGraphEventsService MSGraphEventsService,
                            final Callback<Response> callback) {
                        // create a new event to delete
                        Event event = createEvent();
                        MSGraphEventsService.createNewEvent(
                                getVersion(),
                                event,
                                new Callback<Event>() {
                                    @Override
                                    public void success(Event eventVO, Response response) {
                                        // event created, now let's delete it
                                        MSGraphEventsService.deleteEvent(
                                                getVersion(),
                                                eventVO.id,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        callback.failure(error);
                                    }
                                });
                    }
                }
        };
    }

    public abstract void request(MSGraphEventsService service, Callback<Result> callback);

    private static Event createEvent() {
        Event event = new Event();
        event.subject = "Microsoft Graph API Discussion";

        // set start time to now
        DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = DateTime.now().toString();
        event.start = start;

        // and end in 1 hr
        DateTimeTimeZone end = new DateTimeTimeZone();
        end.dateTime = DateTime.now().plusHours(1).toString();
        event.end = end;

        // set the timezone
        start.timeZone = end.timeZone = "UTC";

        // set a location
        Location location = new Location();
        location.displayName = "Bill's Office";
        event.location = location;

        // add attendees
        Attendee attendee = new Attendee();
        attendee.type = Attendee.TYPE_REQUIRED;
        attendee.emailAddress = new EmailAddress();
        attendee.emailAddress.address = "mara@fabrikam.com";
        event.attendees = new Attendee[]{attendee};

        // add a msg
        ItemBody msg = new ItemBody();
        msg.content = "Let's discuss the power of the Office 365 unified API.";
        msg.contentType = ItemBody.CONTENT_TYPE_TEXT;
        event.body = msg;

        return event;
    }

}