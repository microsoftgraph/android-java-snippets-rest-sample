/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedString;

public interface MSGraphEventsService {

    /**
     * Gets events for the connected user
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/events")
    void getEvents(
            @Path("version") String version,
            Callback<Void> callback
    );

    /**
     * Creates a new event for the connected user
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param body JSON describing the properties of the new event to craete
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/me/events")
    void createNewEvent(
            @Path("version") String version,
            @Body TypedString body,
            Callback<Void> callback
    );

    /**
     * Updates an event owned by the connected user
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param eventId Id of the event to update
     * @param body JSON describing the properties of the updated event to apply with patch
     * @param callback will be called with results of REST operation
     */
    @PATCH("/{version}/me/events/{eventid}")
    void updateEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            @Body TypedString body,
            Callback<Void> callback

    );

    /**
     * Deletes a connected users event
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param eventId Id of the event to delete
     * @param callback will be called with results of REST operation
     */
    @DELETE("/{version}/me/events/{eventid}")
    void deleteEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Void> callback
    );

}