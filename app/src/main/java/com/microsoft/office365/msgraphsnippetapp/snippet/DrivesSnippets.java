/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.microsoftgraphvos.Base;
import com.microsoft.office365.microsoftgraphvos.DriveItem;
import com.microsoft.office365.microsoftgraphvos.Folder;
import com.microsoft.office365.msgraphapiservices.MSGraphDrivesService;

import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.microsoft.office365.msgraphsnippetapp.R.array.create_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.create_me_folder;
import static com.microsoft.office365.msgraphsnippetapp.R.array.delete_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.download_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_drive;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_files;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_organization_drives;
import static com.microsoft.office365.msgraphsnippetapp.R.array.rename_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.update_me_file;

abstract class DrivesSnippets<Result> extends AbstractSnippet<MSGraphDrivesService, Result> {

    private static final String fileContents = "A plain text file";

    public DrivesSnippets(Integer descriptionArray) {
        super(SnippetCategory.drivesSnippetCategory, descriptionArray);
    }

    static DrivesSnippets[] getDrivesSnippets() {
        return new DrivesSnippets[]{
                // Marker element
                new DrivesSnippets(null) {

                    @Override
                    public void request(MSGraphDrivesService msGraphDrivesService,
                                        Callback callback) {
                        //No implementation
                    }
                },
                //Snippets

                /* Get the user's drive
                 * GET https://graph.microsoft.com/{version}/me/drive
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/drive_get
                 */
                new DrivesSnippets<Response>(get_me_drive) {
                    @Override
                    public void request(MSGraphDrivesService msGraphDrivesService,
                                        Callback<Response> callback) {
                        msGraphDrivesService.getDrive(getVersion(), callback);
                    }
                },

                /* Get all of the drives in your tenant
                 * GET https://graph.microsoft.com/{version}/myOrganization/drives
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/drive_get
                 */
                new DrivesSnippets<Response>(get_organization_drives) {
                    @Override
                    public void request(MSGraphDrivesService msGraphDrivesService,
                                        Callback<Response> callback) {
                        msGraphDrivesService.getOrganizationDrives(getVersion(), callback);
                    }
                },
                /*
                 * Get a file
                 * GET https://graph.microsoft.com/{version}/me/drive/root/children
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_list_children
                 */
                new DrivesSnippets<Response>(get_me_files) {
                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Response> callback) {
                        //Get first group
                        msGraphDrivesService.getCurrentUserFiles(getVersion(), callback);
                    }
                },
                /*
                 * Create a file
                 * PUT https://graph.microsoft.com/{version}/me/drive/root/children/{filename}/content
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_post_children
                 */
                new DrivesSnippets<Base>(create_me_file) {
                    @Override
                    public void request(final MSGraphDrivesService msGraphDrivesService,
                                        final Callback<Base> callback) {
                        //Create a new file under root
                        msGraphDrivesService.putNewFile(
                                getVersion(),
                                UUID.randomUUID().toString(),
                                fileContents,
                                callback);
                    }
                },
                /*
                 * Download the content of a file
                 * GET https://graph.microsoft.com/{version}/me/drive/items/{filename}/content
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_downloadcontent
                 */
                new DrivesSnippets<Response>(download_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Response> callback) {
                        // create a new file to download
                        msGraphDrivesService.putNewFile(getVersion(),
                                UUID.randomUUID().toString(),
                                fileContents,
                                new Callback<Base>() {

                                    @Override
                                    public void success(Base file, Response response) {
                                        // download the file we created
                                        msGraphDrivesService.downloadFile(
                                                getVersion(),
                                                file.id,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        // pass along error to original callback
                                        callback.failure(error);
                                    }
                                });
                    }
                },
                /*
                 * Update the content of a file
                 * PUT https://graph.microsoft.com/{version}/me/drive/items/{filename}/content
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_update
                 */
                new DrivesSnippets<Base>(update_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Base> callback) {
                        msGraphDrivesService.putNewFile(getVersion(),
                                UUID.randomUUID().toString(),
                                fileContents,
                                new Callback<Base>() {

                                    @Override
                                    public void success(Base directoryObject, Response response) {
                                        String updatedBody = "Updated file contents";
                                        //download the file we created
                                        msGraphDrivesService.updateFile(
                                                getVersion(),
                                                directoryObject.id,
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
                 * DELETE https://graph.microsoft.com/{version}/me/drive/items/{fileId}/
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_delete
                 */
                new DrivesSnippets<Base>(delete_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Base> callback) {
                        msGraphDrivesService.putNewFile(
                                getVersion(),
                                UUID.randomUUID().toString(),
                                fileContents,
                                new Callback<Base>() {

                                    @Override
                                    public void success(Base file, Response response) {
                                        //download the file we created
                                        msGraphDrivesService.deleteFile(
                                                getVersion(),
                                                file.id,
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
                 * PATCH https://graph.microsoft.com/{version}/me/drive/items/{fileId}/
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_update
                 */
                new DrivesSnippets<Base>(rename_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Base> callback) {
                        msGraphDrivesService.putNewFile(
                                getVersion(),
                                UUID.randomUUID().toString(),
                                fileContents,
                                new Callback<Base>() {

                                    @Override
                                    public void success(Base file, Response response) {
                                        // create a new item
                                        DriveItem delta = new DriveItem();

                                        // give it a random name
                                        delta.name = UUID.randomUUID().toString();

                                        //download the file we created
                                        msGraphDrivesService.renameFile(
                                                getVersion(),
                                                file.id,
                                                delta,
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
                 * Creates a folder
                 * POST https://graph.microsoft.com/me/drive/root/children
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_post_children
                 */
                new DrivesSnippets<Response>(create_me_folder) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Response> callback) {
                        // create a new driveitem
                        DriveItem folder = new DriveItem();
                        // give it a random name
                        folder.name = UUID.randomUUID().toString();
                        // set the folder
                        folder.folder = new Folder();
                        // set the conflict resolution behavior for actions that create
                        // a new item
                        folder.conflictBehavior = "rename";

                        // actually create the folder
                        msGraphDrivesService.createFolder(getVersion(), folder, callback);
                    }
                }
        };
    }

    public abstract void request(MSGraphDrivesService service, Callback<Result> callback);
}