package com.microsoft.office365.microsoftgraphvos;

import com.google.gson.annotations.SerializedName;

public class ItemVO {

    public String name;

    public String id;

    @SerializedName("@name.conflictBehavior")
    public String conflictBehavior;

    public FolderVO folder;

}
