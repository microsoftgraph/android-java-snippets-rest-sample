/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.MessageWrapper;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface MSGraphMailService {

    /**
     * Fetch a user's Messages
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/messages")
    void getMail(
            @Path("version") String version,
            Callback<Response> callback
    );

    /**
     * Creates & sends a new Message
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     The Message object
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/me/microsoft.graph.sendmail")
    void createNewMail(
            @Path("version") String version,
            @Body MessageWrapper body,
            Callback<Response> callback
    );
}