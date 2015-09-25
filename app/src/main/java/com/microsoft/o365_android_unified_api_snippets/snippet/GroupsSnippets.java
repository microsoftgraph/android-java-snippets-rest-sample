/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;


import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.microsoft.unifiedapi.service.UnifiedGroupsService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.delete_a_group;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_a_group;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_all_groups;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_group_members;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_group_owners;
import static com.microsoft.o365_android_unified_api_snippets.R.array.insert_a_group;
import static com.microsoft.o365_android_unified_api_snippets.R.array.update_a_group;

public abstract class GroupsSnippets<Result> extends AbstractSnippet<UnifiedGroupsService, Result> {

    public GroupsSnippets(Integer descriptionArray) {
        super(SnippetCategory.groupSnippetCategory, descriptionArray);
    }

    static GroupsSnippets[] getGroupsSnippets() {
        return new GroupsSnippets[]{
                // Marker element
                new GroupsSnippets(null) {
                    @Override
                    public void request(UnifiedGroupsService service, retrofit.Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /*
                 * Get a group by id
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new GroupsSnippets<Void>(get_a_group) {
                    @Override
                    public void request(final UnifiedGroupsService service, final retrofit.Callback<Void> callback) {
                        //Get first group
                        service.getGroups(getVersion(), ImmutableMap.of("$top", "1"), new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                //Get the group by its ID
                                service.getGroup(
                                        getVersion(),
                                        getGroupId(response),
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


                 /* Get all of the members of the first organization group
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}/members
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new GroupsSnippets<Void>(get_group_members) {
                    @Override
                    public void request(final UnifiedGroupsService service, final retrofit.Callback<Void> callback) {
                        //Get first group
                        service.getGroups(getVersion(), ImmutableMap.of("$top", "1"), new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                //Get members from the group
                                service.getGroupEntities(
                                        getVersion(),
                                        getGroupId(response),
                                        "members",
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

                /* Get all of a group's owners
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}/owners
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new GroupsSnippets<Void>(get_group_owners) {
                    @Override
                    public void request(final UnifiedGroupsService service, final retrofit.Callback<Void> callback) {
                        //Get first group
                        service.getGroups(getVersion(), ImmutableMap.of("$top", "1"), new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                //Update the group we created
                                service.patchGroup(
                                        getVersion(),
                                        getGroupId(response),
                                        createUpdateBody(),
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

                new GroupsSnippets<Void>(get_all_groups) {
                    @Override
                    public void request(UnifiedGroupsService service, retrofit.Callback<Void> callback) {
                        service.getGroups(
                                getVersion(),
                                null,
                                callback);
                    }
                },

                /* Create a new group with a random name
                 * HTTP POST https://graph.microsoft.com/{version}/myOrganization/groups
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new GroupsSnippets<Void>(insert_a_group) {

                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback) {
                        service.insertGroup(
                                getVersion(),
                                createNewGroup(),
                                callback);
                    }
                },

                /* Update a group
                 * HTTP PATCH https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new GroupsSnippets<Void>(update_a_group) {

                    @Override
                    public void request(final UnifiedGroupsService service, final retrofit.Callback<Void> callback) {
                        //Create a group that we will delete
                        service.insertGroup(getVersion(), createNewGroup(), new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                //Update the group we created
                                service.patchGroup(
                                        getVersion(),
                                        getGroupId(response),
                                        createUpdateBody(),
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

                /* Delete a group
                 * HTTP DELETE https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new GroupsSnippets<Void>(delete_a_group) {

                    @Override
                    public void request(final UnifiedGroupsService service, final retrofit.Callback<Void> callback) {
                        //Create a group that we will delete
                        service.insertGroup(getVersion(), createNewGroup(), new Callback<Void>() {

                            @Override
                            public void success(Void aVoid, Response response) {
                                //Delete the group we created
                                service.deleteGroup(
                                        getVersion(),
                                        getGroupId(response),
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //pass along error to original callback
                                callback.failure(error);
                            }
                        });
                    }
                }
        };
    }

    @Override
    public abstract void request(UnifiedGroupsService service, retrofit.Callback<Result> callback);

    /**
     * Creates a Json payload for a POST operation to
     * insert a new group
     *
     * @return TypedString. The Json body
     */
    protected TypedString createNewGroup() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", UUID.randomUUID().toString());
        jsonObject.addProperty("displayName", UUID.randomUUID().toString());
        jsonObject.addProperty("mailEnabled", false);
        jsonObject.addProperty("mailNickname", UUID.randomUUID().toString());
        jsonObject.addProperty("securityEnabled", true);
        return new TypedString(jsonObject.toString()) {
            @Override
            public String mimeType() {
                return "application/json";
            }
        };
    }

    /**
     * Creates a Json object for the body of a PATCH operation
     *
     * @return TypedString version of the JSON body
     */
    protected TypedString createUpdateBody() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", UUID.randomUUID().toString());
        jsonObject.addProperty("mailEnabled", false);
        jsonObject.addProperty("mailNickname", UUID.randomUUID().toString());
        jsonObject.addProperty("securityEnabled", true);
        return new TypedString(jsonObject.toString()) {
            @Override
            public String mimeType() {
                return "application/json";
            }
        };
    }

    /**
     * Gets the group object id from the HTTP response object
     * returned from a group REST call. Method expects that the JSON is a single
     * group object.
     *
     * @param json The JSON to parse. Expected to be a single group object
     * @return The group id (objectID) of the first group found in the array.
     */
    protected String getGroupId(retrofit.client.Response json) {
        if (json == null)
            return "";

        String groupID;

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(json.getBody().in(), "UTF-8"));
            JsonElement responseElement = new JsonParser().parse(reader);
            JsonObject responseObject = responseElement.getAsJsonObject();
            groupID = responseObject.get("objectId").getAsString();
            return groupID;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Gets the first group object id found from the HTTP response object
     * returned from a group REST call. Method expects that the JSON is an array
     * of group objects.
     *
     * @param json The JSON to parse. This is expected to be an array of group objects.
     * @return The group id (objectID) of the first group found in the array.
     */
    protected String getFirstGroupId(retrofit.client.Response json) {
        if (json == null)
            return "";

        String groupID;
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(json.getBody().in(), "UTF-8"));
            JsonElement responseElement = new JsonParser().parse(reader);
            JsonObject responseObject = responseElement.getAsJsonObject();
            JsonArray valueArray = responseObject.getAsJsonArray("value");
            JsonObject groupObject = valueArray.get(0).getAsJsonObject();
            groupID = groupObject.get("objectId").getAsString();
            return groupID;
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