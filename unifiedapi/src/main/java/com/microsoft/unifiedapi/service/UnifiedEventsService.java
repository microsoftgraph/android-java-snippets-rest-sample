/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.unifiedapi.service;

import com.microsoft.unifiedvos.Envelope;
import com.microsoft.unifiedvos.EventVO;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedString;

public interface UnifiedEventsService {

    /**
     * Gets events for the connected user
     * @param version
     * @param callback
     */
    @GET("/{version}/me/events")
    void getEvents(
            @Path("version") String version,
            Callback<String> callback
    );

    /**
     * Accepts an event for the connected user
     * @param version
     * @param eventId
     * @param callback
     */
    @POST("/{version}/me/events/{eventid}/Accept")
    void postAcceptEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Envelope<EventVO>> callback
    );


    /**
     * Tentatively accepts an event for the connected user
     * @param version
     * @param eventId
     * @param callback
     */
    @POST("/{version}/me/events/{eventid}/TentativeAccept")
    void postTentativeAccept(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Envelope<EventVO>> callback
    );


    /**
     * Declines an event for the connected user
     * @param version
     * @param eventId
     * @param callback
     */
    @POST("/{version}/me/events/{eventid}/Decline")
    void postDeclineEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Envelope<EventVO>> callback
    );


    /**
     * Creates a new event for the connected user
     * @param version
     * @param body
     * @param callback
     */
    @POST("/{version}/me/events")
    void postNewEvent(
            @Path("version") String version,
            @Body TypedString body,
            Callback<Envelope<EventVO>> callback
    );


    /**
     * Updates an event owned by the connected user
     * @param version
     * @param eventId
     * @param body
     * @param callback
     */
    @PATCH("/{version}/me/events/{eventid}")
    void putUpdatedEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            @Body TypedString body,
            Callback<Envelope<EventVO>> callback

    );

    /**
     * Deletes a connnected users event
     * @param version
     * @param eventId
     * @param callback
     */
    @DELETE("/{version}/me/events/{eventid}")
    void deleteEvent(
            @Path("version") String version,
            @Path("eventid") String eventId,
            Callback<Envelope<EventVO>> callback
    );

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