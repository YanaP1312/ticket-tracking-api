package net.hackyourfuture.tickettrackingsystem.repositories;

import net.hackyourfuture.tickettrackingsystem.dto.request.users.PostUserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.GetUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.PatchUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.PostUserResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GetUserResponse> getAllUsers(){
        String sql = """
                SELECT user_id, user_name, user_email
                FROM users
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new GetUserResponse(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_email")
                ));
    }

    public Optional<GetUserResponse> getUserById(int userId){
        String sql = """
                SELECT user_id, user_name, user_email
                FROM users
                WHERE user_id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if(rs.next()){
                return Optional.of(new GetUserResponse(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_email")
                ));
            }
            return Optional.empty();
        }, userId);
    }

    public PostUserResponse createUser(PostUserRequest requestBody){
        String sql = """
                INSERT INTO users (user_name, user_email)
                VALUES (?, ?)
                RETURNING user_id, user_name, user_email
                """;

        return jdbcTemplate.query(sql, rs -> {
            rs.next();
            return new PostUserResponse(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("user_email")
            );
        }, requestBody.userName(), requestBody.userEmail());
    }


    public PatchUserResponse updateUser(String userName, String userEmail, int userId){
        String sql = """
                UPDATE users
                SET user_name = ?, user_email = ?
                WHERE user_id = ?
                RETURNING user_id, user_name, user_email
                """;

        return jdbcTemplate.query(sql, rs -> {
            rs.next();
            return new PatchUserResponse(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("user_email")
            );
        }, userName, userEmail, userId);
    }

    public void removeUser(int userId){
        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;
        jdbcTemplate.update(sql, userId);
    }
}
