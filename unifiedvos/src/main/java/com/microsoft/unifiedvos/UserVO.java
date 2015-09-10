/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.unifiedvos;


import org.joda.time.DateTime;

import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

public class UserVO extends BaseDirectoryObjectVO{
    public Boolean accountEnabled;
    public String city;
    public String country;
    public DateTime deletionTimeStamp;
    public String department;
    public Boolean dirSyncEnabled;
    public String displayName;
    public String facsimileTelephoneNumber;
    public String givenName;
    public String immutableId;
    public String jobTitle;
    public DateTime lastDirSyncTime;
    public String mail;
    public String mailNickName;
    public String mobile;
    public UUID objectId;
    public String objectType;
    public String onPremisesSecurityIdentifier;
    public String[] otherMails;
    public String passwordPolicies;
    public PasswordProfileV0 passwordProfile;
    public String physicalDeliveryOfficeName;
    public String postalCode;
    public String preferredLanguage;
    public String[] proxyAddresses;
    public String sipProxyAddress;
    public String state;
    public String streetAddress;
    public String surname;
    public String telephoneNumber;
    public StreamSource thumbnailPhoto;
    public String usageLocation;
    public String userPrincipalName;
    public String userType;




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