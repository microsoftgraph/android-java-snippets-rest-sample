/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
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
// *********************************************************
//
// O365-Android-Microsoft-Graph-Snippets, https://github.com/OfficeDev/O365-Android-Microsoft-Graph-Snippets
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// *********************************************************