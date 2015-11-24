/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.microsoftgraphvos.AttendeeVO;
import com.microsoft.office365.microsoftgraphvos.DateTimeTimeZoneVO;
import com.microsoft.office365.microsoftgraphvos.EmailAddressVO;
import com.microsoft.office365.microsoftgraphvos.Envelope;
import com.microsoft.office365.microsoftgraphvos.EventVO;
import com.microsoft.office365.microsoftgraphvos.ItemBodyVO;
import com.microsoft.office365.microsoftgraphvos.LocationVO;
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
                new EventsSnippets<Envelope<EventVO>>(get_user_events) {

                    @Override
                    public void request(
                            MSGraphEventsService MSGraphEventsService,
                            Callback<Envelope<EventVO>> callback) {
                        MSGraphEventsService.getEvents(getVersion(), callback);
                    }
                },

                /*
                 * Adds an event to the signed-in user\'s calendar.
                 * POST https://graph.microsoft.com/{version}/me/events
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/user_post_events
                 */
                new EventsSnippets<EventVO>(create_event) {

                    @Override
                    public void request(
                            MSGraphEventsService MSGraphEventsService,
                            Callback<EventVO> callback) {
                        MSGraphEventsService.createNewEvent(getVersion(), createEvent(), callback);
                    }

                },
                 /*
                 * Update an event
                 * PATCH https://graph.microsoft.com/{version}/me/events/{Event.Id}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/event_update
                 */
                new EventsSnippets<EventVO>(update_event) {

                    @Override
                    public void request(
                            final MSGraphEventsService MSGraphEventsService,
                            final Callback<EventVO> callback) {
                        // create a new event to update
                        MSGraphEventsService.createNewEvent(
                                getVersion(),
                                createEvent(),
                                new Callback<EventVO>() {
                                    @Override
                                    public void success(EventVO eventVO, Response response) {
                                        // now that the event has been created,
                                        // let's change the subject
                                        EventVO amended = new EventVO();
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
                        EventVO event = createEvent();
                        MSGraphEventsService.createNewEvent(
                                getVersion(),
                                event,
                                new Callback<EventVO>() {
                                    @Override
                                    public void success(EventVO eventVO, Response response) {
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

    private static EventVO createEvent() {
        EventVO event = new EventVO();
        event.subject = "Microsoft Graph API Discussion";

        // set start time to now
        DateTimeTimeZoneVO start = new DateTimeTimeZoneVO();
        start.dateTime = DateTime.now().toString();
        event.start = start;

        // and end in 1 hr
        DateTimeTimeZoneVO end = new DateTimeTimeZoneVO();
        end.dateTime = DateTime.now().plusHours(1).toString();
        event.end = end;

        // set the timezone
        start.timeZone = end.timeZone = "UTC";

        // set a location
        LocationVO location = new LocationVO();
        location.displayName = "Bill's Office";
        event.location = location;

        // add attendees
        AttendeeVO attendee = new AttendeeVO();
        attendee.type = AttendeeVO.TYPE_REQUIRED;
        attendee.emailAddress = new EmailAddressVO();
        attendee.emailAddress.address = "mara@fabrikam.com";
        event.attendees = new AttendeeVO[]{attendee};

        // add a msg
        ItemBodyVO msg = new ItemBodyVO();
        msg.content = "Let's discuss the power of the Office 365 unified API.";
        msg.contentType = ItemBodyVO.CONTENT_TYPE_TEXT;
        event.body = msg;

        return event;
    }

}