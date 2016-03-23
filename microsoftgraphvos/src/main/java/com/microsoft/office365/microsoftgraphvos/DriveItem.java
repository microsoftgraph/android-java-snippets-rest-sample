package com.microsoft.office365.microsoftgraphvos;

import com.google.gson.annotations.SerializedName;

public class DriveItem extends Base {

    public String name;

    @SerializedName("@name.conflictBehavior")
    public String conflictBehavior;

    public Folder folder;

}
