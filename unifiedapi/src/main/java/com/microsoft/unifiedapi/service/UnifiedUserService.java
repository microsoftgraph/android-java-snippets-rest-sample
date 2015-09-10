/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.unifiedapi.service;

import com.microsoft.unifiedvos.BaseDirectoryObjectVO;
import com.microsoft.unifiedvos.Envelope;
import com.microsoft.unifiedvos.Photo;
import com.microsoft.unifiedvos.UserVO;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedString;
public interface UnifiedUserService {

    @GET("/{version}/me")
    void getMe(
            @Path("version") String version,
            Callback<Envelope<UserVO>> callback
    );

    @GET("/{version}/me/userPhoto")
    void getMePhoto(
            @Path("version") String version,
            Callback<Envelope<Photo>> callback
    );

    @GET("/{version}/me/manager")
    void getMeManager(
            @Path("version") String version,
            Callback<Envelope<UserVO>> callback
    );

    @GET("/{version}/me/directReports")
    void getDirectReports(
            @Path("version") String version,
            Callback<Envelope<UserVO>> callback
    );

    @GET("/{version}/me/workingWith")
    void getWorkingWith(
            @Path("version") String version,
            Callback<Envelope<UserVO>> callback
    );

    @GET("/{version}/me/memberOf")
    void getMemberOf(
            @Path("version") String version,
            Callback<Envelope<BaseDirectoryObjectVO>> callback
    );

    @GET("/{version}/myOrganization/users")
    void getUsers(
            @Path("version") String version,
            @Query("$filter") String filter,
            Callback<Envelope<UserVO>> callback
    );

    @POST("/{version}/myOrganization/users")
    void postNewUser(
            @Path("version") String version,
            @Body TypedString body,
            Callback<Envelope<UserVO>> callback
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