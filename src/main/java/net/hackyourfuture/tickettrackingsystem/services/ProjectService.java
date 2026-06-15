package net.hackyourfuture.tickettrackingsystem.services;

import net.hackyourfuture.tickettrackingsystem.dto.request.projects.PatchProjectRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.projects.PostProjectRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.DeleteProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.GetProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.PatchProjectResponse;
import net.hackyourfuture.tickettrackingsystem.dto.response.projects.PostProjectResponse;
import net.hackyourfuture.tickettrackingsystem.exceptions.BadRequestException;
import net.hackyourfuture.tickettrackingsystem.exceptions.NotFoundException;
import net.hackyourfuture.tickettrackingsystem.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository){
        this.repository = repository;
    }

    public List<GetProjectResponse> getAllProjects(){
        return repository.getAllProjects();
    }

    public GetProjectResponse getProjectById(int projectId){
        return repository.getProjectById(projectId)
                .orElseThrow(() -> new NotFoundException("Project with id " + projectId + " not found"));
    }

    public PostProjectResponse createProject(PostProjectRequest projectRequest){
        return repository.createProject(projectRequest);
    }

    public PatchProjectResponse updateProject(int projectId, PatchProjectRequest requestBody){
        GetProjectResponse existing = repository.getProjectById(projectId)
                .orElseThrow(() -> new NotFoundException("Project with id " + projectId + " not found"));

        if(requestBody.projectName() == null || requestBody.projectName().isBlank()){
            throw new BadRequestException("At least one field must be provided.");
        }

        return repository.updateProject(projectId, requestBody);
    }

    public DeleteProjectResponse removeProject(int projectId){
        GetProjectResponse existing = repository.getProjectById(projectId)
                .orElseThrow(() -> new NotFoundException("Project with id " + projectId + " not found"));

        repository.removeProject(projectId);

        return new DeleteProjectResponse("Project with id " + projectId + " successfully removed, and all related tickets to it too");
    }
}
