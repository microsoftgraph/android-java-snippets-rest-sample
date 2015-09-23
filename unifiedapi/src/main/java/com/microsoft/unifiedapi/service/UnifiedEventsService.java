/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.unifiedapi.service;

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
    void postNewEvent(
            @Path("version") String version,
            @Body TypedString body,
            Callback<Void> callback
    );

    /**
     * Creates a new event for the connected user synchronously
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param body JSON describing the properties of the new event to craete
     */
    @POST("/{version}/me/events")
    retrofit.client.Response postNewEventSynchronous(
            @Path("version") String version,
            @Body TypedString body
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
    void patchEvent(
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