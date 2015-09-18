/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.unifiedapi.service;

import com.microsoft.unifiedvos.Envelope;
import com.microsoft.unifiedvos.GroupVO;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedString;

public interface UnifiedGroupsService {

    @GET("/{version}/myOrganization/Groups")
    void getGroups(
            @Path("version") String version,
            Callback<Void> callback
    );

    @GET("/{version}/myOrganization/Groups")
    retrofit.client.Response getTopGroups(
            @Path("version") String version,
            @Query("$top") String top
    );


    @GET("/{version}/myOrganization/Groups")
    retrofit.client.Response getGroups(
            @Path("version") String version
    );

    @GET("/{version}/myOrganization/Groups/{groupId}")
    void getGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Void> callback
    );

    @GET("/{version}/myOrganization/Groups/{groupId}/{entity}")
    void getGroupEntities(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Path("entity") String entity,
            Callback<Void> callback
    );

    @POST("/{version}/myOrganization/Groups/")
    void insertGroupAsync(
            @Path("version") String version,
            @Body TypedString content,
            Callback<Void> callback
    );


    @POST("/{version}/myOrganization/Groups/")
    void insertGroup(
            @Path("version") String version,
            @Body TypedString content,
            Callback<Void> callback
    );

    @POST("/{version}/myOrganization/Groups/")
    retrofit.client.Response insertGroupSynchronous(
            @Path("version") String version,
            @Body TypedString content
    );


    @PATCH("/{version}/myOrganization/Groups/{groupId}")
    void patchGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            @Body TypedString content,
            Callback<Void> callback
    );

    @DELETE("/{version}/myOrganization/Groups/{groupId}")
    void deleteGroup(
            @Path("version") String version,
            @Path("groupId") String groupId,
            Callback<Void> callback
    );

}
// *********************************************************
//
// O365-Android-Unified-API-Snippets, https://github.com/OfficeDev/O365-Android-Unified-API-Snippets
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