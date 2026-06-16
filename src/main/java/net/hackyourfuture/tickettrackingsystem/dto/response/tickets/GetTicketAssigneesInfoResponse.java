package net.hackyourfuture.tickettrackingsystem.dto.response.tickets;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetTicketAssigneesInfoResponse(
        @JsonProperty("user_id")
        int userId,

        @JsonProperty("user_name")
        String userName,

        @JsonProperty("user_email")
        String userEmail
){}
