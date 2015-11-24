/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.EventVO;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;

public interface MSGraphEventsService {

    @GET("/{version}/me/events")
    void getEvents(
            @Path("version") String version,
            Callback<Response> callback
    );

    @POST("/{version}/me/events")
    void createNewEvent(
            @Path("version") String version,
            @Body EventVO body,
            Callback<EventVO> callback
    );

    @PATCH("/{version}/me/events/{eventid}")
    void updateEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            @Body EventVO body,
            Callback<Response> callback

    );

    @DELETE("/{version}/me/events/{eventid}")
    void deleteEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Response> callback
    );

}