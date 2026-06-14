package net.hackyourfuture.tickettrackingsystem.controllers;

import jakarta.validation.Valid;
import net.hackyourfuture.tickettrackingsystem.dto.request.users.PatchUserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.users.PostUserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.DeleteUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.GetUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.PatchUserResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.PostUserResponse;
import net.hackyourfuture.tickettrackingsystem.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

   @GetMapping
   @ResponseStatus(HttpStatus.OK)
   public List<GetUserResponse> getAllUsers(){
        return service.getAllUsers();
   }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetUserResponse getUserById(@PathVariable int id){
        return service.getUserById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PostUserResponse createUser(@Valid @RequestBody PostUserRequest requestBody){
        return service.createUser(requestBody);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatchUserResponse updateUser(@PathVariable int id, @Valid @RequestBody PatchUserRequest requestBody){
        return service.updateUser(id, requestBody);
    }

   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.OK)
    public DeleteUserResponse removeUser(@PathVariable int id){
        return service.removeUser(id);
    }
}
