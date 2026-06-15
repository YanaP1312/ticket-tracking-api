package net.hackyourfuture.tickettrackingsystem.repositories;

import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PatchTicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketRequest;
import net.hackyourfuture.tickettrackingsystem.models.Assignee;
import net.hackyourfuture.tickettrackingsystem.models.Ticket;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class TicketRepository {

    private JdbcTemplate jdbcTemplate;

    public TicketRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ticket> getAllTickets(){
        String sql = """
                SELECT t.ticket_id,
                t.ticket_title,
                t.ticket_description,
                t.project_id,
                t.ticket_status,
                t.ticket_created_at,
                t.ticket_updated_at,
                u.user_id,
                u.user_name
                FROM tickets t
                LEFT JOIN tickets_users tu ON t.ticket_id = tu.ticket_id
                LEFT JOIN users u ON tu.user_id = u.user_id
                ORDER BY t.ticket_id
                """;

        Map<Integer, Ticket> ticketMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            int id = rs.getInt("ticket_id");

            if (!ticketMap.containsKey(id)) {
                ticketMap.put(id, new Ticket(
                        id,
                        rs.getString("ticket_title"),
                        rs.getString("ticket_description"),
                        rs.getInt("project_id"),
                        rs.getString("ticket_status"),
                        rs.getObject("ticket_created_at", LocalDateTime.class),
                        rs.getObject("ticket_updated_at", LocalDateTime.class),
                        new ArrayList<>()
                ));
            }

            if (rs.getObject("user_id") != null) {
                ticketMap.get(id).getAssignees().add(
                        new Assignee(
                                rs.getInt("user_id"),
                                rs.getString("user_name")
                        )
                );
            }
        });
        return new ArrayList<>(ticketMap.values());
    }

    public Optional<Ticket> getTicketById(int ticketId){
        String sql = """
                SELECT t.ticket_id,
                t.ticket_title,
                t.ticket_description,
                t.project_id,
                t.ticket_status,
                t.ticket_created_at,
                t.ticket_updated_at,
                u.user_id,
                u.user_name
                FROM tickets t
                LEFT JOIN tickets_users tu ON t.ticket_id = tu.ticket_id
                LEFT JOIN users u ON tu.user_id = u.user_id
                WHERE t.ticket_id = ?
                """;

        final Ticket[] result = {null};

        jdbcTemplate.query(sql, rs -> {
            if(result[0] == null){
                result[0] = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("ticket_title"),
                        rs.getString("ticket_description"),
                        rs.getInt("project_id"),
                        rs.getString("ticket_status"),
                        rs.getObject("ticket_created_at", LocalDateTime.class),
                        rs.getObject("ticket_updated_at", LocalDateTime.class),
                        new ArrayList<>()
                );
            }

            if(rs.getObject("user_id") !=null){
                result[0].getAssignees().add(
                        new Assignee(
                                rs.getInt("user_id"),
                                rs.getString("user_name")

                        )
                );
            }
        } , ticketId);

        return Optional.ofNullable(result[0]);
    }

    public Ticket createTicket(PostTicketRequest requestBody) {
        String sql = """
                INSERT INTO tickets(ticket_title, ticket_description, project_id, ticket_status)
                VALUES(?, ?, ?, ?)
                RETURNING ticket_id, ticket_title, ticket_description, project_id, ticket_status, ticket_created_at, ticket_updated_at
                """;

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("ticket_title"),
                        rs.getString("ticket_description"),
                        rs.getInt("project_id"),
                        rs.getString("ticket_status"),
                        rs.getObject("ticket_created_at", LocalDateTime.class),
                        rs.getObject("ticket_updated_at", LocalDateTime.class),
                        new ArrayList<>()
                ),
                requestBody.ticketTitle(),
                requestBody.ticketDescription(),
                requestBody.projectId(),
                requestBody.ticketStatus()
        );
    }

    public Ticket updateTicket(int ticketId, PatchTicketRequest requestBody){
        String sql = """
                UPDATE tickets
                SET ticket_title = COALESCE(?, ticket_title),
                ticket_description =  COALESCE(?, ticket_description),
                project_id = COALESCE(?, project_id),
                ticket_status = COALESCE(?, ticket_status),
                ticket_updated_at = NOW()
                WHERE ticket_id = ?
                RETURNING ticket_id, ticket_title, ticket_description, project_id,
                 ticket_status, ticket_created_at, ticket_updated_at
                
                """;
        Ticket ticket = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("ticket_title"),
                        rs.getString("ticket_description"),
                        rs.getInt("project_id"),
                        rs.getString("ticket_status"),
                        rs.getObject("ticket_created_at", LocalDateTime.class),
                        rs.getObject("ticket_updated_at", LocalDateTime.class),
                        new ArrayList<>()
                ),
                requestBody.ticketTitle(),
                requestBody.ticketDescription(),
                requestBody.projectId(),
                requestBody.ticketStatus(),
                ticketId
        );

        ticket.setAssignees(getAssigneesByTickedId(ticketId));
        return ticket;

    }

    public List<Assignee> getAssigneesByTickedId(int ticketId){
        String sql = """
                SELECT u.user_id, u.user_name
                FROM tickets_users tu
                JOIN users u ON tu.user_id = u.user_id
                WHERE tu.ticket_id = ?
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Assignee(
                        rs.getInt("user_id"),
                        rs.getString("user_name")
                ), ticketId);
    }

    public List<Assignee> addAssigneeToTicket(int ticketId, int userId){
        String sql = """
                INSERT INTO tickets_users(ticket_id, user_id)
                VALUES(?, ?)
                """;

        jdbcTemplate.update(sql, ticketId, userId);

        return getAssigneesByTickedId(ticketId);
    }

    public List<Assignee> deleteAssigneeFromTicket(int ticketId, int userId){
        String sql = """
                DELETE FROM tickets_users
                WHERE ticket_id = ? AND user_id= ?
                """;

        jdbcTemplate.update(sql, ticketId, userId);

        return getAssigneesByTickedId(ticketId);
    }
}
