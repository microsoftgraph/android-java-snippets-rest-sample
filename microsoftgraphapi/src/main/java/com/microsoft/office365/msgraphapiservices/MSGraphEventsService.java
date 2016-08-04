/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.Event;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MSGraphEventsService {

    /**
     * GET a user's Events
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/me/events")
    Call<ResponseBody> getEvents(
            @Path("version") String version
    );

    /**
     * Create a new Event
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     The Event to create
     */
    @POST("/{version}/me/events")
    Call<ResponseBody> createNewEvent(
            @Path("version") String version,
            @Body Event body
    );

    /**
     * Update an Event
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param eventId  The unique id of the Event to update
     * @param body     The updated Event object
     */
    @PATCH("/{version}/me/events/{eventid}")
    Call<ResponseBody> updateEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            @Body Event body

    );

    /**
     * Delete an Event
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param eventId  The unique id of the Event to delete
     */
    @DELETE("/{version}/me/events/{eventid}")
    Call<ResponseBody> deleteEvent(
            @Path("version") String version,
            @Path("eventid") String eventId
    );

}
