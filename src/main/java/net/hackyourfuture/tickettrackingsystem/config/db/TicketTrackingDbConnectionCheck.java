package net.hackyourfuture.tickettrackingsystem.config.db;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TicketTrackingDbConnectionCheck {

    private final JdbcTemplate jdbcTemplate ;

    public TicketTrackingDbConnectionCheck(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkConnection(){
        String result = this.jdbcTemplate.queryForObject("SELECT 'connected'", String.class);
        System.out.println("Database status: " + result);
    }
}
