package net.hackyourfuture.tickettrackingsystem.dto.response.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteTicketAssigneeResponse (
        @JsonProperty("ticket_id")
        int ticketId,
        GetTicketAssigneesInfoResponse assignees,
        String warning
){
}
