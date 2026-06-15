package net.hackyourfuture.tickettrackingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private int ticketId;
    private String ticketTitle;
    private String ticketDescription;
    private int projectId;
    private String ticketStatus;
    private LocalDateTime ticketCreatedAt;
    private LocalDateTime ticketUpdatedAt;
}
