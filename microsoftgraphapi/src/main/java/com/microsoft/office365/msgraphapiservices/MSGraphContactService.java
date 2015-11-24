/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MSGraphContactService {

    /**
     * Get the connected user's contacts.
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     * @param callback will be called with results of REST operation
     */
    @GET("/{version}/myOrganization/contacts")
    void getContacts(
            @Path("version") String version,
            Callback<Response> callback
    );

}