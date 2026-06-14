package net.hackyourfuture.tickettrackingsystem.dto.request.projects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record PatchProjectRequest (
        @JsonProperty("project_name")
        @Size(min=3, message="project_name must be at least 3 characters long")
        String projectName
){}
