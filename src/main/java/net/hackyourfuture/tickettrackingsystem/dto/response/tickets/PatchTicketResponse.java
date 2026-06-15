package net.hackyourfuture.tickettrackingsystem.dto.response.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record PatchTicketResponse (
        @JsonProperty("ticket_id")
        int ticketId,
        @JsonProperty("ticket_title")
        String ticketTitle,
        @JsonProperty("ticket_description")
        String ticketDescription,
        @JsonProperty("project_id")
        int projectId,
        @JsonProperty("ticket_status")
        String ticketStatus,
        @JsonProperty("ticket_created_at")
        LocalDateTime ticketCreatedAt,
        @JsonProperty("ticket_updated_at")
        LocalDateTime ticketUpdatedAt,
        List<GetTicketAssigneesInfoResponse> assignees
) {}
