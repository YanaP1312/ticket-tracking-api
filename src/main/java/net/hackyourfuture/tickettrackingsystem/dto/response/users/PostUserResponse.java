package net.hackyourfuture.tickettrackingsystem.dto.response.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostUserResponse (
        @JsonProperty("user_id")
        int userId,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("user_email")
        String userEmail){
}