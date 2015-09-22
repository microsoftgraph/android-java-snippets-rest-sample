/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.unifiedapi.service.UnifiedGroupsService;
import com.microsoft.unifiedvos.Envelope;
import com.microsoft.unifiedvos.GroupVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;
import timber.log.Timber;

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


                new GroupsSnippets<Void>(get_a_group) {
                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback) {

                        final PlaceToStash stash = new PlaceToStash();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                //Get the first group to obtain an ID later
                                retrofit.client.Response response = service.getTopGroups(getVersion(),"1");
                                stash.resp = response;
                            }
                        };
                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getFirstGroupId(stash.resp);

                            //Get the group by its ID
                            service.getGroup(
                                    getVersion(),
                                    groupID,
                                    callback);

                        } catch (InterruptedException e) {
                            // report this error back to our callback
                            e.printStackTrace();
                        }

                    }
                },

                /**
                 * Gets all of the members of the first organization group
                 */
                new GroupsSnippets<Void>(get_group_members) {
                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback) {

                        final PlaceToStash stash = new PlaceToStash();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                //Get first group
                                retrofit.client.Response response = service.getTopGroups(getVersion(),"1");
                                stash.resp = response;
                            }
                        };

                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getFirstGroupId(stash.resp);

                            //Get members from the group
                            service.getGroupEntities(
                                    getVersion(),
                                    groupID,
                                    "members",
                                    callback);

                        } catch (InterruptedException e) {
                            // report this error back to our callback
                            e.printStackTrace();
                        }

                    }
                },

                /**
                 * Gets all of a group's owners
                 */
                new GroupsSnippets<Void>(get_group_owners) {
                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback) {

                        final PlaceToStash stash = new PlaceToStash();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                //Get first group
                                retrofit.client.Response response = service.getTopGroups(getVersion(),"1");
                                stash.resp = response;
                            }
                        };

                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getFirstGroupId(stash.resp);

                            //Get owners of the specified group ID
                            service.getGroupEntities(
                                    getVersion(),
                                    groupID,
                                    "owners",
                                    callback);

                        } catch (InterruptedException e) {
                            // report this error back to our callback
                            e.printStackTrace();
                        }
                    }
                },

                new GroupsSnippets<Void>(get_all_groups) {
                    @Override
                    public void request(UnifiedGroupsService service, retrofit.Callback<Void> callback) {
                        service.getGroups(
                                getVersion(),
                                callback);
                    }
                },

                /**
                 * Creates a new group with a random name
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

                /**
                 * Updates a group
                 */
                new GroupsSnippets<Void>(update_a_group) {

                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback) {
                        final PlaceToStash stash = new PlaceToStash();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                //insert a group that we will update later
                                retrofit.client.Response response = service.insertGroupSynchronous(
                                        getVersion(),
                                        createNewGroup());
                                stash.resp = response;
                            }
                        };
                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getObjectId(stash.resp);

                            //update the group we created
                            service.patchGroup(
                                    getVersion(),
                                    groupID,
                                    createUpdateBody(),
                                    callback);
                        } catch (InterruptedException e) {
                            // report this error back to our callback
                            e.printStackTrace();
                        }
                    }
                },

                /**
                 * Deletes a group
                 */
                new GroupsSnippets<Void>(delete_a_group) {

                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback) {

                        final PlaceToStash stash = new PlaceToStash();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                //create a group that we can delete
                                retrofit.client.Response response = service.insertGroupSynchronous(
                                        getVersion(),
                                        createNewGroup());
                                stash.resp = response;
                            }
                        };
                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = getObjectId(stash.resp);
                            //delete the group we created earlier
                            service.deleteGroup(
                                    getVersion(),
                                    groupID,
                                    callback);
                        } catch (InterruptedException e) {
                            // report this error back to our callback
                            e.printStackTrace();
                        }
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
     * @return
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
     * Gets the directory object id from the HTTP response object
     * returned from a group REST call
     *
     * @param json
     * @return String object id
     */
    protected String getObjectId(retrofit.client.Response json) {
        if (json == null)
            return "";

        String groupID = null;
        Gson gson = new Gson();
        GroupVO group = gson.fromJson(
                getStringFromResponse(json),
                GroupVO.class);
        groupID = group.objectId;

        return groupID;
    }

    protected String getFirstGroupId(retrofit.client.Response json){
        if (json == null)
            return "";

        String groupID;

        JsonElement responseElement = new JsonParser().parse(getStringFromResponse(json));
        JsonObject responseObject = responseElement.getAsJsonObject();
        JsonArray valueArray = responseObject.getAsJsonArray("value");
        JsonObject groupObject = valueArray.get(0).getAsJsonObject();
        groupID = groupObject.get("objectId").getAsString();

        return groupID;
    }

    protected String getStringFromResponse(retrofit.client.Response json){
        if (json == null)
            return "";

        try {
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(
                            json.getBody().in()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();

        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }


    class PlaceToStash {
        public retrofit.client.Response resp;
        public IOException wentWrong;
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