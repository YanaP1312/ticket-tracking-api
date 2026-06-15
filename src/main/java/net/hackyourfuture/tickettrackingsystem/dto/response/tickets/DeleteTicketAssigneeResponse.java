package net.hackyourfuture.tickettrackingsystem.dto.response.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DeleteTicketAssigneeResponse (
        @JsonProperty("ticket_id")
        int ticketId,
        List<GetTicketAssigneesInfoResponse> assignees,
        String warning
){}
