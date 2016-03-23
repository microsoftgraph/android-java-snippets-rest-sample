/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.Envelope;
import com.microsoft.office365.microsoftgraphvos.Event;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;

public interface MSGraphEventsService {

    /**
     * GET a user's Events
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/events")
    void getEvents(
            @Path("version") String version,
            Callback<Envelope<Event>> callback
    );

    /**
     * Create a new Event
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     The Event to create
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/me/events")
    void createNewEvent(
            @Path("version") String version,
            @Body Event body,
            Callback<Event> callback
    );

    /**
     * Update an Event
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param eventId  The unique id of the Event to update
     * @param body     The updated Event object
     * @param callback will be called with results of REST operation
     */
    @PATCH("/{version}/me/events/{eventid}")
    void updateEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            @Body Event body,
            Callback<Event> callback

    );

    /**
     * Delete an Event
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param eventId  The unique id of the Event to delete
     * @param callback will be called with results of REST operation
     */
    @DELETE("/{version}/me/events/{eventid}")
    void deleteEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Response> callback
    );

}