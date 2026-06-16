package net.hackyourfuture.tickettrackingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignee {
    private int userId;
    private String userName;
    private String userEmail;
}
