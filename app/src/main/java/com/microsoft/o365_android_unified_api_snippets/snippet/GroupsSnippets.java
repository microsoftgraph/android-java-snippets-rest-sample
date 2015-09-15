/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.o365_android_unified_api_snippets.snippet;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.unifiedapi.service.UnifiedGroupsService;
import com.microsoft.unifiedvos.GroupVO;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import retrofit.*;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.get_all_groups;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_a_group;
import static com.microsoft.o365_android_unified_api_snippets.R.array.insert_a_group;
import static com.microsoft.o365_android_unified_api_snippets.R.array.update_a_group;

public abstract class GroupsSnippets <Result> extends AbstractSnippet<UnifiedGroupsService, Result> {

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

                /**
                 * Gets all of the user's notebooks
                 */
                new GroupsSnippets<Void>(get_a_group) {
                    @Override
                    public void request(UnifiedGroupsService service, retrofit.Callback<Void> callback) {
                        service.getGroup(
                                getVersion(),
                                "", //TODO: get a group id
                                callback);
                    }
                },
                // Snippets

                /**
                 * Gets all of the user's notebooks
                 */
                new GroupsSnippets<Void>(get_all_groups) {
                    @Override
                    public void request(UnifiedGroupsService service, retrofit.Callback<Void> callback) {
                        service.getGroups(
                                getVersion(),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Creates a new group with a random name
                 */
                new GroupsSnippets<Void>(insert_a_group) {

                    @Override
                    public void request(UnifiedGroupsService service, retrofit.Callback<Void> callback) {

                        service.insertGroupAsync(
                                getVersion(),
                                createNewGroup(),
                                callback);
                    }
                },
                // Snippets

                /**
                 * Updates a group
                 */
                new GroupsSnippets<Void>(update_a_group) {


                    @Override
                    public void request(final UnifiedGroupsService service, retrofit.Callback<Void> callback)  {
                         class PlaceToStash {
                             public retrofit.client.Response resp;
                             public IOException wentWrong;
                        }

                        final PlaceToStash stash = new PlaceToStash();
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                retrofit.client.Response call = service.insertGroup(
                                            getVersion(),
                                            createNewGroup());
                                    stash.resp = call;
                            }
                        };
                        Thread exec = new Thread(task);
                        exec.start();
                        try {
                            exec.join();
                            String groupID = parseForValue("groupId",
                                    stash.resp);
                            service.patchGroup(
                                    getVersion(),
                                    groupID,
                                    createUpdateBody(),
                                    callback);
                        }
                        catch (InterruptedException e) {
                            // report this error back to our callback
                            e.printStackTrace();
                        }
                    }
                }
        };
    }

    @Override
    public abstract void request(UnifiedGroupsService service, retrofit.Callback<Result> callback);


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

    protected  TypedString createUpdateBody() {
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
    protected String parseForValue(String jsonKey, retrofit.client.Response json)
    {
        String groupID = null;
        TypedInput body = json.getBody();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(body.in()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Log.i("response", total.toString());
            Gson gson = new Gson();
            GroupVO group = gson.fromJson(total.toString(), GroupVO.class);
            groupID = group.objectId;
            Log.i("group id ", group.objectId);
        }
        catch (IOException ex){}
        return groupID;
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