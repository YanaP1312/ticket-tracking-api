package net.hackyourfuture.tickettrackingsystem.dto.request.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUserRequest(
        @JsonProperty("user_name")
        @NotBlank(message = "user_name is required")
        @Size(min = 3, message = "user_name must be at least 3 characters long")
        String userName,

        @JsonProperty("user_email")
        @NotBlank(message = "user_email is required")
        @Email(message = "user_email must be a valid email format")
        String userEmail
) {}
