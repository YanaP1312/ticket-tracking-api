package net.hackyourfuture.tickettrackingsystem.models.enums;

public enum TicketStatus {
    OPEN("open"),
    IN_PROGRESS("in progress"),
    CLOSED("closed");

    private final String dbValue;

    TicketStatus(String dbValue){
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
