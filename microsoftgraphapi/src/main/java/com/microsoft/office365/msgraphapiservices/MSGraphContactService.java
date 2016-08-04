/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphapiservices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MSGraphContactService {

    /**
     * Get the connected user's contacts.
     *
     * @param version  The version of the API to use (beta, v1, etc...)
     */
    @GET("/{version}/myOrganization/contacts")
    Call<ResponseBody> getContacts(
            @Path("version") String version
    );
}
