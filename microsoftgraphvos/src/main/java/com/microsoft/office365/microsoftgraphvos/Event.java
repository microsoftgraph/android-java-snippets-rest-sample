package com.microsoft.office365.microsoftgraphvos;

public class Event extends Base {

    public String subject;
    public ItemBody body;
    public DateTimeTimeZone start;
    public DateTimeTimeZone end;
    public Location location;
    public Attendee[] attendees;
}
