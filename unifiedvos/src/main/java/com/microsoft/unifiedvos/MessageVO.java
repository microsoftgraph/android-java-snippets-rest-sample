/*
*  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
*/
package com.microsoft.unifiedvos;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class MessageVO {
    @SerializedName("Attachments")
    public BaseAttachmentVO[] attachments;
    @SerializedName("BccRecipients")
    public Recipient[] bccRecipients;
    @SerializedName("Body")
    public ItemBodyVO body;
    @SerializedName("BodyPreview")
    public String bodyPreview;
    @SerializedName("Categories")
    public String[] categories;
    @SerializedName("CcRecipients")
    public Recipient[] ccRecipients;
    @SerializedName("ChangeKey")
    public String changeKey;
    @SerializedName("ConversationId")
    public String conversationId;
    @SerializedName("DateTimeCreated")
    public DateTime dateTimeCreated;
    @SerializedName("DateTimeLastModified")
    public DateTime dateTimeLastModified;
    @SerializedName("DateTimeReceived")
    public DateTime dateTimeReceived;
    @SerializedName("DateTimeSent")
    public DateTime dateTimeSent;
    @SerializedName("From")
    public Recipient from;
    @SerializedName("HasAttachments")
    public Boolean hasAttachments;
    @SerializedName("Importance")
    public String importance;
    @SerializedName("IsDeliveryReceiptRequested")
    public Boolean isDeliveryReceiptRequested;
    @SerializedName("IsDraft")
    public Boolean isDraft;
    @SerializedName("IsRead")
    public Boolean isRead;
    @SerializedName("IsReadReceiptRequested")
    public Boolean isReadReceiptRequested;
    @SerializedName("MultiValueExtendedProperties")
    public Object[] multiValueExtendedProperties;
    @SerializedName("ParentFolderId")
    public String parentFolderId;
    @SerializedName("ReplyTo")
    public Recipient[] replyTo;
    @SerializedName("Sender")
    public Recipient sender;
    @SerializedName("SingleValueExtendedProperties")
    public Object[] singleValueExtendedProperties;
    @SerializedName("Subject")
    public String subject;
    @SerializedName("ToRecipients")
    public Recipient[] toRecipients;
    @SerializedName("UniqueBody")
    public ItemBodyVO uniqueBody;
    @SerializedName("WebLink")
    public String webLink;

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