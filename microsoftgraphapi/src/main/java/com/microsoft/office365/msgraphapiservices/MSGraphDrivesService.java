/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import com.microsoft.office365.microsoftgraphvos.DriveItem;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MSGraphDrivesService {

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/me/drive")
    Call<ResponseBody> getDrive(
            @Path("version") String version
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/myOrganization/drives")
    Call<ResponseBody> getOrganizationDrives(
            @Path("version") String version
    );

    /**
     * Gets children file metadata of the root folder
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/me/drive/root/children")
    Call<ResponseBody> getCurrentUserFiles(
            @Path("version") String version
    );

    /**
     * Creates a new file under the root folder
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filename The name of the file to create
     * @param value    The file contents
     */
    @PUT("/{version}/me/drive/root/children/{filename}/content")
    Call<ResponseBody> putNewFile(
            @Path("version") String version,
            @Path("filename") String filename,
            @Body String value
    );

    /**
     * Downloads a file
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param filename The name of the file to download
     */
    @GET("/{version}/me/drive/items/{filename}/content")
    Call<ResponseBody> downloadFile(
            @Path("version") String version,
            @Path("filename") String filename
    );

    /**
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param fileId   The unique id of the file to update
     * @param value    The updated contents of the file to update
     */
    @PUT("/{version}/me/drive/items/{fileId}/content")
    Call<ResponseBody> updateFile(
            @Path("version") String version,
            @Path("fileId") String fileId,
            @Body String value
    );

    /**
     * Delete a file
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param fileId   The unique id of the file to delete
     */
    @DELETE("/{version}/me/drive/items/{fileId}/")
    Call<ResponseBody> deleteFile(
            @Path("version") String version,
            @Path("fileId") String fileId
    );

    /**
     * Rename a file
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param fileId   The unique id of the file to delete
     * @param body     The updated object - fields with differing values from the server-side version will be updated
     */
    @PATCH("/{version}/me/drive/items/{fileId}/")
    Call<ResponseBody> renameFile(
            @Path("version") String version,
            @Path("fileId") String fileId,
            @Body DriveItem body
    );

    /**
     * Create a new folder
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param body     The folder metadata to use in the creation
     */
    @POST("/{version}/me/drive/root/children")
    Call<ResponseBody> createFolder(
            @Path("version") String version,
            @Body DriveItem body
    );
}
