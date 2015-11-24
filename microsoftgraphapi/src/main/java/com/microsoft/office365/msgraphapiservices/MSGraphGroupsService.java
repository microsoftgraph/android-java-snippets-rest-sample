/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.Envelope;
import com.microsoft.office365.microsoftgraphvos.GroupVO;

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

    @GET("/{version}/myOrganization/Groups")
    void getGroups(
            @Path("version") String version,
            @QueryMap Map<String, String> filters,
            Callback<Envelope<GroupVO>> callback
    );

    @GET("/{version}/myOrganization/Groups/{groupId}")
    void getGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<GroupVO> callback
    );

    @GET("/{version}/myOrganization/Groups/{groupId}/{entity}")
    void getGroupEntities(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Path("entity") String entity,
            Callback<Void> callback
    );

    @POST("/{version}/myOrganization/Groups/")
    void createGroup(
            @Path("version") String version,
            @Body GroupVO content,
            Callback<GroupVO> callback
    );

    @PATCH("/{version}/myOrganization/Groups/{groupId}")
    void updateGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Body GroupVO content,
            Callback<GroupVO> callback
    );

    @DELETE("/{version}/myOrganization/Groups/{groupId}")
    void deleteGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Response> callback
    );

}