package net.hackyourfuture.tickettrackingsystem.controllers;

import jakarta.validation.Valid;
import net.hackyourfuture.tickettrackingsystem.dto.request.projects.PatchProjectRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.projects.PostProjectRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.DeleteProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.GetProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.PatchProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.PostProjectResponse;
import net.hackyourfuture.tickettrackingsystem.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service){
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GetProjectResponse> getAllProjects(){
        return service.getAllProjects();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetProjectResponse getProjectById(@PathVariable int id){
        return service.getProjectById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostProjectResponse createProject(@Valid @RequestBody PostProjectRequest requestBody){
        return service.createProject(requestBody);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatchProjectResponse updateProject(@PathVariable int id, @Valid @RequestBody PatchProjectRequest requestBody){
        return service.updateProject(id, requestBody);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DeleteProjectResponse removeProject(@PathVariable int id){
        return service.removeProject(id);
    }
}
