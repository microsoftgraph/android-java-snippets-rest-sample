/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MSGraphMeService {

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me")
    void getMe(
            @Path("version") String version,
            Callback<Response> callback
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param select   A set of names specifying which properties to return in results
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me")
    void getMeResponsibilities(
            @Path("version") String version,
            @Query("$select") String select,
            Callback<Response> callback
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param entity   Which entity to retrieve (manager, direct reports, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/{entity}")
    void getMeEntities(
            @Path("version") String version,
            @Path("entity") String entity,
            Callback<Response> callback
    );
}