package com.microsoft.office365.microsoftgraphvos;

import com.google.gson.annotations.SerializedName;

public class MessageWrapper {

    @SerializedName("Message")
    public Message message;

    public boolean saveToSentItems;
}
