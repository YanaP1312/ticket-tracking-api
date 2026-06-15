package net.hackyourfuture.tickettrackingsystem.dto.request.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public record PatchTicketRequest (
        @JsonProperty("ticket_title")
        String ticketTitle,
        @JsonProperty("ticket_description")
        String ticketDescription,
        @JsonProperty("project_id")
        Integer projectId,
        @JsonProperty("ticket_status")
        @Pattern(
                regexp = "open|in progress|closed",
                message = "ticket_status must be one of: open, in progress, closed"
        )
        String ticketStatus
){}
