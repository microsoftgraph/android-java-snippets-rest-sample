package com.microsoft.office365.microsoftgraphvos;

import com.google.gson.annotations.SerializedName;

public class MessageWrapperVO {

    @SerializedName("Message")
    public MessageVO message;

    public boolean saveToSentItems;
}
