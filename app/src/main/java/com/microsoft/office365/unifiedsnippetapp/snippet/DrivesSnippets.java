/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedsnippetapp.snippet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.microsoft.office365.unifiedapiservices.UnifiedDrivesService;

import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static com.microsoft.office365.unifiedsnippetapp.R.array.create_me_file;
import static com.microsoft.office365.unifiedsnippetapp.R.array.download_me_file;
import static com.microsoft.office365.unifiedsnippetapp.R.array.update_me_file;
import static com.microsoft.office365.unifiedsnippetapp.R.array.delete_me_file;
import static com.microsoft.office365.unifiedsnippetapp.R.array.copy_me_file;
import static com.microsoft.office365.unifiedsnippetapp.R.array.rename_me_file;
import static com.microsoft.office365.unifiedsnippetapp.R.array.get_me_drive;
import static com.microsoft.office365.unifiedsnippetapp.R.array.get_me_files;
import static com.microsoft.office365.unifiedsnippetapp.R.array.get_organization_drives;

abstract class DrivesSnippets<Result> extends AbstractSnippet<UnifiedDrivesService, Result> {

    public DrivesSnippets(Integer descriptionArray) {
        super(SnippetCategory.drivesSnippetCategory, descriptionArray);
    }

    static DrivesSnippets[] getDrivesSnippets() {
        return new DrivesSnippets[]{
                // Marker element
                new DrivesSnippets(null) {

                    @Override
                    public void request(UnifiedDrivesService o, retrofit.Callback callback) {
                        //No implementation
                    }
                },
                //Snippets

                /* Get the user's drive
                 * HTTP GET https://graph.microsoft.com/{version}/me/drive
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_drives
                 */
                new DrivesSnippets<Void>(get_me_drive) {
                    @Override
                    public void request(UnifiedDrivesService service, retrofit.Callback<Void> callback) {
                        service.getDrive(
                                getVersion(),
                                callback);
                    }
                },

                 /* Get all of the drives in your tenant
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/drives
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_drives
                 */
                new DrivesSnippets<Void>(get_organization_drives) {
                    @Override
                    public void request(UnifiedDrivesService service, retrofit.Callback<Void> callback) {
                        service.getOrganizationDrives(
                                getVersion(),
                                callback);
                    }
                },
                 /*
                 * Get a file
                 * HTTP GET https://graph.microsoft.com/{version}/...
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new DrivesSnippets<Void>(get_me_files) {
                    @Override
                    public void request(final UnifiedDrivesService service, final retrofit.Callback<Void> callback) {
                        //Get first group
                        service.getCurrentUserFiles(getVersion(), callback);
                    }
                },
                 /*
                 * Create a file
                 * HTTP GET https://graph.microsoft.com/{version}/...
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new DrivesSnippets<Void>(create_me_file) {
                    @Override
                    public void request(final UnifiedDrivesService service, final retrofit.Callback<Void> callback) {
                        //Create a new file under root
                        TypedString fileContents = new TypedString("file contents");
                        service.putNewFile(getVersion(), java.util.UUID.randomUUID().toString(), fileContents, callback);
                    }
                },
                /*
                 * Download the content of a file
                 * HTTP GET https://graph.microsoft.com/{version}/me/drive/items/{filename}/content
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new DrivesSnippets<Void>(download_me_file) {

                    @Override
                    public void request(
                            final UnifiedDrivesService unifiedDrivesService,
                            final Callback<Void> callback) {
                        TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        unifiedDrivesService.putNewFile(getVersion(), java.util.UUID.randomUUID().toString(), body, new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                //download the file we created
                                unifiedDrivesService.downloadFile(
                                        getVersion(),
                                        getFileId(response),
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //pass along error to original callback
                                callback.failure(error);
                            }
                        });
                    }
                },
                /*
                 * Update the content of a file
                 * HTTP PUT https://graph.microsoft.com/{version}/me/drive/items/{filename}/content
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new DrivesSnippets<Void>(update_me_file) {

                    @Override
                    public void request(
                            final UnifiedDrivesService unifiedDrivesService,
                            final Callback<Void> callback) {
                          final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        unifiedDrivesService.putNewFile(getVersion(), java.util.UUID.randomUUID().toString(), body, new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                final TypedString updatedBody = new TypedString("Updated file contents") {
                                    @Override
                                    public String mimeType() {
                                        return "application/json";
                                    }
                                };
                                //download the file we created
                                unifiedDrivesService.updateFile(
                                        getVersion(),
                                        getFileId(response),
                                        updatedBody,
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //pass along error to original callback
                                callback.failure(error);
                            }
                        });
                    }
                },
                /*
                 * Delete the content of a file
                 * HTTP DELETE https://graph.microsoft.com/{version}/me/drive/items/{fileId}/
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new DrivesSnippets<Void>(delete_me_file) {

                    @Override
                    public void request(
                            final UnifiedDrivesService unifiedDrivesService,
                            final Callback<Void> callback) {
                        final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        unifiedDrivesService.putNewFile(getVersion(), java.util.UUID.randomUUID().toString(), body, new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {

                                //download the file we created
                                unifiedDrivesService.deleteFile(
                                        getVersion(),
                                        getFileId(response),
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //pass along error to original callback
                                callback.failure(error);
                            }
                        });
                    }
                },
                 /*
                 * Copies a file
                 * HTTP POST https://graph.microsoft.com/{version}/me/drive/items/{fileId}/Microsoft.Graph.Copy
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new DrivesSnippets<Void>(copy_me_file) {

                    @Override
                    public void request(
                            final UnifiedDrivesService unifiedDrivesService,
                            final Callback<Void> callback) {
                        final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        unifiedDrivesService.putNewFile(getVersion(), java.util.UUID.randomUUID().toString(), body, new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {


                                // Build contents of post body and convert to StringContent object.
                                // Using line breaks for readability.
                                String postBody = "{'parentReference':{"
                                        + "'path':'" + "/drive/root:'},"
                                        + "'name':'" + java.util.UUID.randomUUID().toString() + "'}";

                                final TypedString body = new TypedString(postBody){
                                    @Override
                                    public String mimeType() { return "application/json";}
                                };
                                //download the file we created
                                unifiedDrivesService.copyFile(
                                        getVersion(),
                                        getFileId(response),
                                        body,
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //pass along error to original callback
                                callback.failure(error);
                            }
                        });
                    }
                },
                /*
                 * Renames a file
                 * HTTP PATCH https://graph.microsoft.com/{version}/me/drive/items/{fileId}/
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_Event
                 */
                new DrivesSnippets<Void>(rename_me_file) {

                    @Override
                    public void request(
                            final UnifiedDrivesService unifiedDrivesService,
                            final Callback<Void> callback) {
                        final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        unifiedDrivesService.putNewFile(getVersion(), java.util.UUID.randomUUID().toString(), body, new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {


                                // Build contents of post body and convert to StringContent object.
                                // Using line breaks for readability.
                                String patchBody = "{"
                                        + "'name':'" + java.util.UUID.randomUUID().toString() + "'}";

                                final TypedString body = new TypedString(patchBody){
                                    @Override
                                    public String mimeType() { return "application/json";}
                                };
                                //download the file we created
                                unifiedDrivesService.renameFile(
                                        getVersion(),
                                        getFileId(response),
                                        body,
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //pass along error to original callback
                                callback.failure(error);
                            }
                        });
                    }
                },
        };
    }

    public abstract void request(UnifiedDrivesService unifiedDrivesService, Callback<Result> callback);

    protected String getFileId(retrofit.client.Response json) {
        if (json == null)
            return "";

        String fileId;

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(json.getBody().in(), "UTF-8"));
            JsonElement responseElement = new JsonParser().parse(reader);
            JsonObject responseObject = responseElement.getAsJsonObject();
            fileId = responseObject.get("id").getAsString();
            return fileId;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
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