/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.Envelope;
import com.microsoft.office365.microsoftgraphvos.Group;

import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface MSGraphGroupsService {

    /**
     * GET a user's Groups
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filters  The criteria around which the Groups should be filtered
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/Groups")
    void getGroups(
            @Path("version") String version,
            @QueryMap Map<String, String> filters,
            Callback<Envelope<Group>> callback
    );

    /**
     * GET a specific Group by id
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The id of the Group to GET
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/Groups/{groupId}")
    void getGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Group> callback
    );

    /**
     * Gets the contents of a Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The Group to interrogate
     * @param entity   Type type of Entity to fetch from this group
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/Groups/{groupId}/{entity}")
    void getGroupEntities(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Path("entity") String entity,
            Callback<Response> callback
    );

    /**
     * Create a new Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param content  The Group to create
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/myOrganization/Groups/")
    void createGroup(
            @Path("version") String version,
            @Body Group content,
            Callback<Group> callback
    );

    /**
     * Update a Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The unique id of the group to update
     * @param content  The updated metadata of this Group
     * @param callback will be called with results of REST operation
     */
    @PATCH("/{version}/myOrganization/Groups/{groupId}")
    void updateGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Body Group content,
            Callback<Group> callback
    );

    /**
     * Delete a Group
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param groupId  The unique Group id to delete
     * @param callback will be called with results of REST operation
     */
    @DELETE("/{version}/myOrganization/Groups/{groupId}")
    void deleteGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Response> callback
    );

}