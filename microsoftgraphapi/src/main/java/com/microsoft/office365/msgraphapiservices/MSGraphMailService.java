/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.MessageWrapper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MSGraphMailService {

    /**
     * Fetch a user's Messages
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/me/messages")
    Call<ResponseBody> getMail(
            @Path("version") String version
    );

    /**
     * Creates & sends a new Message
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     The Message object
     */
    @POST("/{version}/me/microsoft.graph.sendmail")
    Call<ResponseBody> createNewMail(
            @Path("version") String version,
            @Body MessageWrapper body
    );
}
