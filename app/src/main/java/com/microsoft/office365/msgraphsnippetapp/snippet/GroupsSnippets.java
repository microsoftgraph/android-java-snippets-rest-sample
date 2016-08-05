/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.microsoftgraphvos.Group;
import com.microsoft.office365.msgraphapiservices.MSGraphGroupsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.microsoft.office365.msgraphsnippetapp.R.array.delete_a_group;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_a_group;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_all_groups;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_group_members;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_group_owners;
import static com.microsoft.office365.msgraphsnippetapp.R.array.insert_a_group;
import static com.microsoft.office365.msgraphsnippetapp.R.array.update_a_group;

public abstract class GroupsSnippets<Result> extends AbstractSnippet<MSGraphGroupsService, Result> {

    public GroupsSnippets(Integer descriptionArray) {
        super(SnippetCategory.groupSnippetCategory, descriptionArray);
    }

    static GroupsSnippets[] getGroupsSnippets() {
        return new GroupsSnippets[]{
                // Marker element
                new GroupsSnippets(null) {
                    @Override
                    public void request(MSGraphGroupsService service, Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /*
                 * Get a group by id
                 * GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_get
                 */
                new GroupsSnippets<ResponseBody>(get_a_group) {
                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<ResponseBody> callback) {
                        // create a group then query it
                        service.createGroup(getVersion(), createGroup()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String groupId = new JSONObject(response.body().string()).getString("id");
                                    // request the newly created group
                                    service.getGroup(getVersion(), groupId).enqueue(callback);
                                } catch(JSONException | IOException e) {
                                    callback.onFailure(call, e);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                callback.onFailure(call, t);
                            }
                        });
                    }
                },
                /* Get all of the members of a newly created organization group
                 * GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}/members
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_list_members
                 */
                new GroupsSnippets<ResponseBody>(get_group_members) {
                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<ResponseBody> callback) {
                        // create a group then ask for its members
                        service.createGroup(getVersion(), createGroup()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String groupId = new JSONObject(response.body().string()).getString("id");
                                    service.getGroupEntities(
                                            getVersion(),
                                            groupId,
                                            "members").enqueue(callback);
                                } catch(JSONException | IOException e) {
                                    callback.onFailure(call, e);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Timber.e(t.getMessage(), this);
                            }
                        });
                    }
                },

                /* Get all of a group's owners
                 * GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}/owners
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_list_owners
                 */
                new GroupsSnippets<ResponseBody>(get_group_owners) {
                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<ResponseBody> callback) {
                        // create a group and then request its owner
                        service.createGroup(getVersion(), createGroup()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String groupId = new JSONObject(response.body().string()).getString("id");
                                    service.getGroupEntities(
                                            getVersion(),
                                            groupId,
                                            "owners").enqueue(callback);
                                } catch(JSONException | IOException e) {
                                    callback.onFailure(call, e);
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Timber.e(t.getMessage(), this);
                            }
                        });
                    }
                },
                /* List all organization groups
                 * GET https://graph.microsoft.com/v1.0/groups
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_list
                 */
                new GroupsSnippets<ResponseBody>(get_all_groups) {
                    @Override
                    public void request(MSGraphGroupsService service,
                                        Callback<ResponseBody> callback) {
                        Map<String, String> filters = new HashMap<>();
                        service.getGroups(getVersion(), filters).enqueue(callback);
                    }
                },

                /* Create a new group with a random name
                 * POST https://graph.microsoft.com/{version}/myOrganization/groups
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/group
                 */
                new GroupsSnippets<ResponseBody>(insert_a_group) {

                    @Override
                    public void request(final MSGraphGroupsService service,
                                        Callback<ResponseBody> callback) {
                        service.createGroup(getVersion(), createGroup()).enqueue(callback);
                    }
                },

                /* Update a group
                 * PATCH https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_update
                 */
                new GroupsSnippets<ResponseBody>(update_a_group) {

                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<ResponseBody> callback) {
                        //Create a group that we will update
                        service.createGroup(getVersion(), createGroup()).enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String groupId = new JSONObject(response.body().string()).getString("id");
                                    Group amended = new Group();
                                    amended.displayName = "A renamed group";
                                    //Update the group we created
                                    service.updateGroup(
                                            getVersion(),
                                            groupId,
                                            amended).enqueue(callback);
                                } catch(JSONException | IOException e) {
                                    callback.onFailure(call, e);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                //pass along error to original callback
                                Timber.e(t.getMessage(), this);
                            }
                        });
                    }
                },

                /* Delete a group
                 * DELETE https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_delete
                 */
                new GroupsSnippets<ResponseBody>(delete_a_group) {

                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<ResponseBody> callback) {
                        //Create a group that we will delete
                        service.createGroup(getVersion(), createGroup()).enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String groupId = new JSONObject(response.body().string()).getString("id");
                                    //Delete the group we created
                                    service.deleteGroup(getVersion(), groupId).enqueue(callback);
                                } catch(JSONException | IOException e) {
                                    callback.onFailure(call, e);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                //pass along error to original callback
                                Timber.e(t.getMessage(), this);
                            }
                        });
                    }
                }
        };
    }

    @Override
    public abstract void request(MSGraphGroupsService service, Callback<Result> callback);

    public static Group createGroup() {
        Group group = new Group();
        group.displayName = group.mailNickname = UUID.randomUUID().toString();
        return group;
    }

}