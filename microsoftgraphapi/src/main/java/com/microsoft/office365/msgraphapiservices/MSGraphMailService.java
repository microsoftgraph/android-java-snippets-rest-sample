/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.MessageWrapperVO;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface MSGraphMailService {

    @GET("/{version}/me/messages")
    void getMail(
            @Path("version") String version,
            Callback<Response> callback
    );

    @POST("/{version}/me/microsoft.graph.sendmail")
    void createNewMail(
            @Path("version") String version,
            @Body MessageWrapperVO body,
            Callback<Response> callback
    );
}