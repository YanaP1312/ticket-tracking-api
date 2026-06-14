package net.hackyourfuture.tickettrackingsystem.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record PatchUserRequest(
        @Size(min = 3, message = "user_name must be at least 3 characters long")
        String userName,

        @Email(message = "user_email must be a valid email format")
        String userEmail) {
}
