package net.hackyourfuture.tickettrackingsystem.services;

import net.hackyourfuture.tickettrackingsystem.dto.response.projects.GetProjectResponse;
import net.hackyourfuture.tickettrackingsystem.exceptions.NotFoundException;
import net.hackyourfuture.tickettrackingsystem.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository repository;

    public ProjectService(ProjectRepository repository){
        this.repository = repository;
    }

    public List<GetProjectResponse> getAllProjects(){
        return repository.getAllProjects();
    }

    public boolean isProjectExist(int projectId){
        if(!repository.projectExists(projectId)){
            throw new NotFoundException("Project with id " + projectId + " not found");
        }

        return true;
    }
}
