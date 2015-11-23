/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedString;

public interface MSGraphMailService {

    /**
     * Gets the connected users mail messages
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/messages")
    void getMail(
            @Path("version") String version,
            Callback<Void> callback
    );

    /**
     * Sends a mail message for the connected user
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param body JSON describing the propeties of the message to send
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/me/sendMail")
    void createNewMail(
            @Path("version") String version,
            @Body TypedString body,
            Callback<Void> callback
    );
}