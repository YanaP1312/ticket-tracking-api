package net.hackyourfuture.tickettrackingsystem.dto.response.projects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PatchProjectResponse(
        @JsonProperty("project_id")
        int projectId,

        @JsonProperty("project_name")
        String projectName
) {}
