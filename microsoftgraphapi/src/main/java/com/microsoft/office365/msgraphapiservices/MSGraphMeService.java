/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MSGraphMeService {

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/me")
    Call<ResponseBody> getMe(
            @Path("version") String version
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param select   A set of names specifying which properties to return in results
     */
    @GET("/{version}/me")
    Call<ResponseBody> getMeResponsibilities(
            @Path("version") String version,
            @Query("$select") String select
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param entity   Which entity to retrieve (manager, direct reports, etc...)
     */
    @GET("/{version}/me/{entity}")
    Call<ResponseBody> getMeEntities(
            @Path("version") String version,
            @Path("entity") String entity
    );
}
