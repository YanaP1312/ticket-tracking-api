package net.hackyourfuture.tickettrackingsystem.dto.response.projects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetTicketStatusResponse (
        int open,

        @JsonProperty("in_progress")
        int inProgress,

        int closed
){}
