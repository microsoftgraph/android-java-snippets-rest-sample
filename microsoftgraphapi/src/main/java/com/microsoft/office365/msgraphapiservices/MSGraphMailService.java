/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.MessageVO;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface MSGraphMailService {

    @GET("/{version}/me/messages")
    void getMail(
            @Path("version") String version,
            Callback<Void> callback
    );

    @POST("/{version}/me/sendMail")
    void createNewMail(
            @Path("version") String version,
            @Body MessageVO body,
            Callback<Void> callback
    );
}