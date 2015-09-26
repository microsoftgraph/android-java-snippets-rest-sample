/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.office365.unifiedsnippetapp.snippet;

import com.microsoft.office365.unifiedsnippetapp.services.UnifiedDrivesService;
import retrofit.Callback;

import static com.microsoft.office365.unifiedsnippetapp.R.array.get_me_drive;
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
                }
        };
    }

    public abstract void request(UnifiedDrivesService unifiedDrivesService, Callback<Result> callback);
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