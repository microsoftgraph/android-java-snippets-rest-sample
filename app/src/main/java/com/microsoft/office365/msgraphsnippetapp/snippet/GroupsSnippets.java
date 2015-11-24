/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.microsoftgraphvos.Envelope;
import com.microsoft.office365.microsoftgraphvos.GroupVO;
import com.microsoft.office365.msgraphapiservices.MSGraphGroupsService;

import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
                new GroupsSnippets<GroupVO>(get_a_group) {
                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<GroupVO> callback) {
                        // create a group then query it
                        service.createGroup(getVersion(), createGroup(), new Callback<GroupVO>() {
                            @Override
                            public void success(GroupVO groupVO, Response response) {
                                // request the newly created group
                                service.getGroup(getVersion(), groupVO.id, callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                callback.failure(error);
                            }
                        });
                    }
                },
                /* Get all of the members of a newly created organization group
                 * GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}/members
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_list_members
                 */
                new GroupsSnippets<Response>(get_group_members) {
                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<Response> callback) {
                        // create a group then ask for its members
                        service.createGroup(getVersion(), createGroup(), new Callback<GroupVO>() {
                            @Override
                            public void success(GroupVO groupVO, Response response) {
                                service.getGroupEntities(
                                        getVersion(),
                                        groupVO.id,
                                        "members",
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                callback.failure(error);
                            }
                        });
                    }
                },

                /* Get all of a group's owners
                 * GET https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}/owners
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_list_owners
                 */
                new GroupsSnippets<Response>(get_group_owners) {
                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<Response> callback) {
                        // create a group and then request its owner
                        service.createGroup(getVersion(), createGroup(), new Callback<GroupVO>() {
                            @Override
                            public void success(GroupVO groupVO, Response response) {
                                service.getGroupEntities(
                                        getVersion(),
                                        groupVO.id,
                                        "owners",
                                        callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                callback.failure(error);
                            }
                        });
                    }
                },
                /* List all organization groups
                 * GET https://graph.microsoft.com/v1.0/groupshttps://graph.microsoft.com/v1.0/groups
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_list
                 */
                new GroupsSnippets<Envelope<GroupVO>>(get_all_groups) {
                    @Override
                    public void request(MSGraphGroupsService service,
                                        Callback<Envelope<GroupVO>> callback) {
                        service.getGroups(getVersion(), null, callback);
                    }
                },

                /* Create a new group with a random name
                 * POST https://graph.microsoft.com/{version}/myOrganization/groups
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/resources/group
                 */
                new GroupsSnippets<GroupVO>(insert_a_group) {

                    @Override
                    public void request(final MSGraphGroupsService service,
                                        Callback<GroupVO> callback) {
                        service.createGroup(getVersion(), createGroup(), callback);
                    }
                },

                /* Update a group
                 * PATCH https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_update
                 */
                new GroupsSnippets<GroupVO>(update_a_group) {

                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<GroupVO> callback) {
                        //Create a group that we will update
                        service.createGroup(getVersion(), createGroup(), new Callback<GroupVO>() {

                            @Override
                            public void success(GroupVO group, Response response) {
                                GroupVO amended = new GroupVO();
                                amended.displayName = "A renamed group";
                                //Update the group we created
                                service.updateGroup(
                                        getVersion(),
                                        group.id,
                                        amended,
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
                 * DELETE https://graph.microsoft.com/{version}/myOrganization/groups/{Group.objectId}
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/group_delete
                 */
                new GroupsSnippets<Response>(delete_a_group) {

                    @Override
                    public void request(final MSGraphGroupsService service,
                                        final Callback<Response> callback) {
                        //Create a group that we will delete
                        service.createGroup(getVersion(), createGroup(), new Callback<GroupVO>() {

                            @Override
                            public void success(GroupVO group, Response response) {
                                //Delete the group we created
                                service.deleteGroup(getVersion(), group.id, callback);
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
    public abstract void request(MSGraphGroupsService service, Callback<Result> callback);

    private static GroupVO createGroup() {
        GroupVO group = new GroupVO();
        group.displayName = group.mailNickname = UUID.randomUUID().toString();
        return group;
    }

}