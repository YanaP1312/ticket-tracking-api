package net.hackyourfuture.tickettrackingsystem.repositories;

import net.hackyourfuture.tickettrackingsystem.dto.response.projects.GetProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.GetTicketStatusResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {
    private JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GetProjectResponse> getAllProjects(){
        String sql = """
                SELECT
                p.project_id,
                p.project_name,
                COUNT(t.ticket_id) FILTER (WHERE t.ticket_status = 'open') AS open,
                COUNT(t.ticket_id) FILTER (WHERE t.ticket_status = 'in progress') AS in_progress,
                COUNT(t.ticket_id) FILTER (WHERE t.ticket_status = 'closed') AS closed
                FROM projects p
                LEFT JOIN tickets t ON t.project_id = p.project_id
                GROUP BY p.project_id, p.project_name
                ORDER BY p.project_id
                """;

        return jdbcTemplate.query(sql, ( (rs, rowNum) ->
                new GetProjectResponse(
                        rs.getInt("project_id"),
                        rs.getString("project_name"),
                        new GetTicketStatusResponse(
                                rs.getInt("open"),
                                rs.getInt("in_progress"),
                                rs.getInt("closed")
                        )
                )
        ));
    }

    public boolean projectExists(int projectId){
        String sql = "SELECT COUNT(*) FROM projects WHERE project_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return count != null && count > 0;
    }

}
