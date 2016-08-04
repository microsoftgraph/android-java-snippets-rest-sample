/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MSGraphUserService {

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filter   An expression specifying criteria for which set of results should be returned
     */
    @GET("/{version}/myOrganization/users")
    Call<ResponseBody> getFilteredUsers(
            @Path("version") String version,
            @Query("$filter") String filter
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/myOrganization/users")
    Call<ResponseBody> getUsers(
            @Path("version") String version
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     JSON describing properties of the new user
     */
    @POST("/{version}/myOrganization/users")
    Call<ResponseBody> createNewUser(
            @Path("version") String version,
            @Body User body
    );
}
