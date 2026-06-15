package net.hackyourfuture.tickettrackingsystem.dto.request.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PostTicketRequest(
        @JsonProperty("ticket_title")
        @NotBlank(message = "ticket_title is required")
        String ticketTitle,
        @JsonProperty("ticket_description")
        String ticketDescription,
        @JsonProperty("project_id")
        @NotNull(message = "project_id is required")
        Integer projectId,
        @JsonProperty("ticket_status")
        @Pattern(
                regexp = "open|in progress|closed",
                message = "ticket_status must be one of: open, in progress, closed"
        )
        @NotBlank(message = "ticket_status is required")
        String ticketStatus
) {}
