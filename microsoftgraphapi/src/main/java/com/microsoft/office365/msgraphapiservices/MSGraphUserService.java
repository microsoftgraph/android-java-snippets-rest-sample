/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.User;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MSGraphUserService {

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filter   An expression specifying criteria for which set of results should be returned
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/users")
    void getFilteredUsers(
            @Path("version") String version,
            @Query("$filter") String filter,
            Callback<Response> callback
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/users")
    void getUsers(
            @Path("version") String version,
            Callback<Response> callback
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     JSON describing properties of the new user
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/myOrganization/users")
    void createNewUser(
            @Path("version") String version,
            @Body User body,
            Callback<Response> callback
    );
}