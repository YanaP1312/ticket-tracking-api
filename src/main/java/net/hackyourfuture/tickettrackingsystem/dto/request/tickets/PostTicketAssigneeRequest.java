package net.hackyourfuture.tickettrackingsystem.dto.request.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record PostTicketAssigneeRequest(
        @JsonProperty("user_id")
        @NotNull(message = "user_id is required")
        Integer userId
) {}
