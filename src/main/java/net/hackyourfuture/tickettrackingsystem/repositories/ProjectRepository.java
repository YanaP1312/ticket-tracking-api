package net.hackyourfuture.tickettrackingsystem.repositories;

import net.hackyourfuture.tickettrackingsystem.dto.request.projects.PatchProjectRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.projects.PostProjectRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.*;
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

    public Optional<GetProjectResponse> getProjectById(int projectId){
        String sql = """
                SELECT
                p.project_id,
                p.project_name,
                COUNT(t.ticket_id) FILTER (WHERE t.ticket_status = 'open') AS open,
                COUNT(t.ticket_id) FILTER (WHERE t.ticket_status = 'in progress') AS in_progress,
                COUNT(t.ticket_id) FILTER (WHERE t.ticket_status = 'closed') AS closed
                FROM projects p
                LEFT JOIN tickets t ON t.project_id = p.project_id
                WHERE p.project_id = ?
                GROUP BY p.project_id, p.project_name
                """;

        return jdbcTemplate.query(sql, ( (rs  -> {
            if(rs.next()) {
                return Optional.of(new GetProjectResponse(
                        rs.getInt("project_id"),
                        rs.getString("project_name"),
                        new GetTicketStatusResponse(
                                rs.getInt("open"),
                                rs.getInt("in_progress"),
                                rs.getInt("closed")
                        )));
            }
               return Optional.empty();
            }
        )));
    }

    public PostProjectResponse createProject(PostProjectRequest requestBody){
        String sql = """
                INSERT INTO projects (project_name)
                VALUES (?)
                RETURNING project_id, project_name
                """;

        return jdbcTemplate.query(sql, rs -> {
            rs.next();
            return new PostProjectResponse(
                    rs.getInt("project_id"),
                    rs.getString("project_name")
            );
            }, requestBody.projectName());

    }

    public PatchProjectResponse updateProject(int projectId, PatchProjectRequest requestBody){
        String sql = """
                UPDATE projects
                SET project_name = ?
                WHERE project_id = ?
                RETURNING project_id, project_name
                """;

        return jdbcTemplate.query(sql,rs -> {
            rs.next();
            return new PatchProjectResponse(
                    rs.getInt("project_id"),
                    rs.getString("project_name")
            );
        }, requestBody.projectName(), projectId);
    }

    public void removeProject(int projectId){
        String sql = """
                DELETE FROM projects
                WHERE project_id = ?
                """;

        jdbcTemplate.update(sql, projectId);
    }

    public boolean projectExists(int projectId){
        String sql = "SELECT COUNT(*) FROM projects WHERE project_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return count != null && count > 0;
    }

}
