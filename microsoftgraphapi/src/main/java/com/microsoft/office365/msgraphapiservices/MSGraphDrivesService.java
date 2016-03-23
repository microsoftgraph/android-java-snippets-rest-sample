/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.Base;
import com.microsoft.office365.microsoftgraphvos.DriveItem;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface MSGraphDrivesService {

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/drive")
    void getDrive(
            @Path("version") String version,
            Callback<Response> callback
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/drives")
    void getOrganizationDrives(
            @Path("version") String version,
            Callback<Response> callback
    );

    /**
     * Gets children file metadata of the root folder
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/drive/root/children")
    void getCurrentUserFiles(
            @Path("version") String version,
            Callback<Response> callback
    );

    /**
     * Creates a new file under the root folder
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filename The name of the file to create
     * @param value    The file contents
     * @param callback will be called with results of REST operation
     */
    @PUT("/{version}/me/drive/root/children/{filename}/content")
    void putNewFile(
            @Path("version") String version,
            @Path("filename") String filename,
            @Body String value,
            Callback<Base> callback
    );

    /**
     * Downloads a file
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filename The name of the file to download
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/drive/items/{filename}/content")
    void downloadFile(
            @Path("version") String version,
            @Path("filename") String filename,
            Callback<Response> callback
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param fileId   The unique id of the file to update
     * @param value    The updated contents of the file to update
     * @param callback will be called with results of REST operation
     */
    @PUT("/{version}/me/drive/items/{fileId}/content")
    void updateFile(
            @Path("version") String version,
            @Path("fileId") String fileId,
            @Body String value,
            Callback<Base> callback
    );

    /**
     * Delete a file
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param fileId   The unique id of the file to delete
     * @param callback will be called with results of REST operation
     */
    @DELETE("/{version}/me/drive/items/{fileId}/")
    void deleteFile(
            @Path("version") String version,
            @Path("fileId") String fileId,
            Callback<Base> callback
    );

    /**
     * Rename a file
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param fileId   The unique id of the file to delete
     * @param body     The updated object - fields with differing values from the server-side version will be updated
     * @param callback will be called with results of REST operation
     */
    @PATCH("/{version}/me/drive/items/{fileId}/")
    void renameFile(
            @Path("version") String version,
            @Path("fileId") String fileId,
            @Body DriveItem body,
            Callback<Base> callback
    );

    /**
     * Create a new folder
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     The folder metadata to use in the creation
     * @param callback will be called with results of REST operation
     */
    @POST("/{version}/me/drive/root/children")
    void createFolder(
            @Path("version") String version,
            @Body DriveItem body,
            Callback<Response> callback
    );
}