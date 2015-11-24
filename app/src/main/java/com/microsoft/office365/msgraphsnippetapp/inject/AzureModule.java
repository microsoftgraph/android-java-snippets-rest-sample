/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.inject;

import com.microsoft.office365.auth.AzureADModule;
import com.microsoft.office365.msgraphsnippetapp.SignInActivity;
import com.microsoft.office365.msgraphsnippetapp.SnippetDetailActivity;
import com.microsoft.office365.msgraphsnippetapp.SnippetDetailFragment;
import com.microsoft.office365.msgraphsnippetapp.SnippetListActivity;

import dagger.Module;

@Module(includes = AzureADModule.class,
        complete = false,
        injects = {
                SignInActivity.class,
                SnippetDetailActivity.class,
                SnippetListActivity.class,
                SnippetDetailFragment.class
        }
)
public class AzureModule {}