package com.microsoft.office365.microsoftgraphvos;

import com.google.gson.annotations.SerializedName;

public class DriveItemVO extends BaseVO{

    public String name;

    @SerializedName("@name.conflictBehavior")
    public String conflictBehavior;

    public FolderVO folder;

}
