/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.mime.TypedString;

public interface MSGraphGroupsService {

    /**
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/Groups")
    void getGroups(
            @Path("version") String version,
            @QueryMap Map<String,String> filters,
            Callback<Void> callback
    );

    /**
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param groupId Id of the group to return
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/Groups/{groupId}")
    void getGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Void> callback
    );

    /**
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param groupId The Id of the group being queries
     * @param entity Which entity to retrieve (members, owners, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/Groups/{groupId}/{entity}")
    void getGroupEntities(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Path("entity") String entity,
            Callback<Void> callback
    );

    /**
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param content JSON describing the properties of the new group
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/myOrganization/Groups/")
    void createGroup(
            @Path("version") String version,
            @Body TypedString content,
            Callback<Void> callback
    );

    /**
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param groupId Id of the group to update
     * @param content JSON describing properties of the updated group
     * @param callback will be called with results of REST operation
     */
    @PATCH("/{version}/myOrganization/Groups/{groupId}")
    void updateGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Body TypedString content,
            Callback<Void> callback
    );

    /**
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param groupId Id of the group to delete
     * @param callback will be called with results of REST operation
     */
    @DELETE("/{version}/myOrganization/Groups/{groupId}")
    void deleteGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Void> callback
    );

}