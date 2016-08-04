/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.Group;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MSGraphGroupsService {

    /**
     * GET a user's Groups
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filters  The criteria around which the Groups should be filtered
     */
    @GET("/{version}/myOrganization/Groups")
    Call<ResponseBody> getGroups(
            @Path("version") String version,
            @QueryMap Map<String, String> filters
    );

    /**
     * GET a specific Group by id
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The id of the Group to GET
     */
    @GET("/{version}/myOrganization/Groups/{groupId}")
    Call<ResponseBody> getGroup(
            @Path("version") String version,
            @Path("groupId") String groupId
    );

    /**
     * Gets the contents of a Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The Group to interrogate
     * @param entity   Type type of Entity to fetch from this group
     */
    @GET("/{version}/myOrganization/Groups/{groupId}/{entity}")
    Call<ResponseBody> getGroupEntities(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Path("entity") String entity
    );

    /**
     * Create a new Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param content  The Group to create
     */
    @POST("/{version}/myOrganization/Groups/")
    Call<ResponseBody> createGroup(
            @Path("version") String version,
            @Body Group content
    );

    /**
     * Update a Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The unique id of the group to update
     * @param content  The updated metadata of this Group
     */
    @PATCH("/{version}/myOrganization/Groups/{groupId}")
    Call<ResponseBody> updateGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Body Group content
    );

    /**
     * Delete a Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The unique Group id to delete
     */
    @DELETE("/{version}/myOrganization/Groups/{groupId}")
    Call<ResponseBody> deleteGroup(
            @Path("version") String version,
            @Path("groupId") String groupId
    );

}
