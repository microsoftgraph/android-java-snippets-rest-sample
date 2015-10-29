/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedapiservices;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface UnifiedFilesService {
    /**
     * Gets events for the connected user
     *
     * @param version The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/me/drive/root/children")
    void getCurrentUserFiles(
            @Path("version") String version,
            Callback<Void> callback
    );
}
