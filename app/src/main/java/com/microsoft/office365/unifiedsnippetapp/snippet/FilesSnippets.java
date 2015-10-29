/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedsnippetapp.snippet;

import com.google.common.collect.ImmutableMap;
import com.microsoft.office365.unifiedapiservices.UnifiedFilesService;
import com.microsoft.office365.unifiedapiservices.UnifiedGroupsService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.microsoft.office365.unifiedsnippetapp.R.array.get_a_group;
import static com.microsoft.office365.unifiedsnippetapp.R.array.get_me_files;

public abstract class FilesSnippets<Result> extends AbstractSnippet<UnifiedFilesService, Result>{
    /**
     * Snippet constructor
     *
     * @param descriptionArray The String array for the specified snippet
     */
    public FilesSnippets(Integer descriptionArray) {
        super(SnippetCategory.filesSnippetCategory,descriptionArray);
    }

    static FilesSnippets[] getFilesSnippets() {
        return new FilesSnippets[]{
                // Marker element
                new FilesSnippets(null) {
                    @Override
                    public void request(UnifiedFilesService service, retrofit.Callback callback) {
                        // Not implemented
                    }
                },
                // Snippets

                /*
                 * Get a file
                 * HTTP GET https://graph.microsoft.com/{version}/...
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entitySet_groups
                 */
                new FilesSnippets<Void>(get_me_files) {
                    @Override
                    public void request(final UnifiedFilesService service, final retrofit.Callback<Void> callback) {
                        //Get first group
                        service.getCurrentUserFiles(getVersion(), callback);
                    }
                }
        };
    }
    @Override
    public abstract void request(UnifiedFilesService service, retrofit.Callback<Result> callback);

}
