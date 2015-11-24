package com.microsoft.office365.microsoftgraphvos;

public class EventVO extends BaseVO {

    public String subject;
    public ItemBodyVO body;
    public DateTimeTimeZoneVO start;
    public DateTimeTimeZoneVO end;
    public LocationVO location;
    public AttendeeVO[] attendees;
}
