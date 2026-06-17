package net.hackyourfuture.tickettrackingsystem.services;

import net.hackyourfuture.tickettrackingsystem.dto.request.users.PatchUserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.users.PostUserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.DeleteUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.GetUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.PatchUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.PostUserResponse;
import net.hackyourfuture.tickettrackingsystem.exceptions.BadRequestException;
import net.hackyourfuture.tickettrackingsystem.exceptions.NotFoundException;
import net.hackyourfuture.tickettrackingsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public List<GetUserResponse> getAllUsers(){
        return repository.getAllUsers();
    }

    public GetUserResponse getUserById(int userId){
        return repository.getUserById(userId).orElseThrow(() ->  new NotFoundException("User with id " + userId + " is not found."));
    }

    public PostUserResponse createUser(PostUserRequest requestBody){
        return repository.createUser(requestBody);
    }

    public PatchUserResponse updateUser(int userId, PatchUserRequest requestBody){
        repository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not found."));

        if((requestBody.userName() == null || requestBody.userName().isBlank()) &&
                (requestBody.userEmail() == null || requestBody.userEmail().isBlank())){
            throw new BadRequestException("At least one field must be provided.");
        }

        return repository.updateUser(requestBody.userName(), requestBody.userEmail(), userId);
    }

    public DeleteUserResponse removeUser(int userId){
        repository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not found."));

        repository.removeUser(userId);

        return new DeleteUserResponse("User with id " + userId + " successfully deleted.");
    }
}
