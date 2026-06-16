package net.hackyourfuture.tickettrackingsystem.services;

import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PatchTicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketAssigneeRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.tickets.*;
import net.hackyourfuture.tickettrackingsystem.exceptions.BadRequestException;
import net.hackyourfuture.tickettrackingsystem.exceptions.ConflictException;
import net.hackyourfuture.tickettrackingsystem.exceptions.NotFoundException;
import net.hackyourfuture.tickettrackingsystem.models.Assignee;
import net.hackyourfuture.tickettrackingsystem.models.Ticket;
import net.hackyourfuture.tickettrackingsystem.models.enums.TicketStatus;
import net.hackyourfuture.tickettrackingsystem.repositories.ProjectRepository;
import net.hackyourfuture.tickettrackingsystem.repositories.TicketRepository;
import net.hackyourfuture.tickettrackingsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final  TicketRepository repository;
    private final  ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ResendService resendService;

    public TicketService(TicketRepository repository, ProjectRepository projectRepository, UserRepository userRepository, ResendService resentService){
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.resendService = resentService;
    }

    public List<GetTicketResponse> getAllTickets(String text, String status){

        String dbStatus = null;

        if(status != null){
            try{
                dbStatus = TicketStatus.valueOf(status.toUpperCase()).getDbValue();
            } catch(IllegalArgumentException e){
                throw new BadRequestException("Status must be one of: open, in_progress, closed");
            }
        }

        return repository.getAllTickets(text, dbStatus)
                .stream()
                .map(this::toGetTicketResponse)
                .toList();
    }

    public GetTicketResponse getTicketById(int ticketId){
        Ticket ticket = repository.getTicketById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket with id " + ticketId + " doesn't exist"));

        return toGetTicketResponse(ticket);

    }

    public PostTicketResponse createTicket(PostTicketRequest requestBody){
        projectRepository.getProjectById(requestBody.projectId())
                .orElseThrow(() -> new NotFoundException(
                        "Project with id " + requestBody.projectId() + " doesn't exist"
                ));

        Ticket ticket = repository.createTicket(requestBody);

        return new PostTicketResponse(
                ticket.getTicketId(),
                ticket.getTicketTitle(),
                ticket.getTicketDescription(),
                ticket.getProjectId(),
                ticket.getTicketStatus(),
                ticket.getTicketCreatedAt(),
                ticket.getTicketUpdatedAt(),
                new ArrayList<>()
        );
    }

    public PatchTicketResponse updateTicket(int ticketId, PatchTicketRequest requestBody){
        repository.getTicketById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket with id " + ticketId + " doesn't exist"));

        if (requestBody.ticketTitle() == null && requestBody.ticketDescription() == null
                && requestBody.projectId() == null && requestBody.ticketStatus() == null) {
            throw new BadRequestException("At least one field must be provided");
        }

        if (requestBody.projectId() != null) {
            projectRepository.getProjectById(requestBody.projectId())
                    .orElseThrow(() -> new NotFoundException(
                            "Project with id " + requestBody.projectId() + " doesn't exist"
                    ));
        }

        Ticket ticket = repository.updateTicket(ticketId, requestBody);

        String warning = sendEmailOrGetWarning(ticket.getAssignees(), ticket,
                "Ticket updated successfully, but email notification could not be sent.");

        return new PatchTicketResponse(
                ticket.getTicketId(),
                ticket.getTicketTitle(),
                ticket.getTicketDescription(),
                ticket.getProjectId(),
                ticket.getTicketStatus(),
                ticket.getTicketCreatedAt(),
                ticket.getTicketUpdatedAt(),
                toAssigneeDtos(ticket.getAssignees()),
                warning
        );
    }

    public PostTicketAssigneeResponse addAssigneeToTicket(int ticketId, PostTicketAssigneeRequest requestBody){
        repository.getTicketById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket with id " + ticketId + " doesn't exist"));

        userRepository.getUserById(requestBody.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + requestBody.userId() + " doesn't exist"));

        if (repository.isUserAssigned(ticketId, requestBody.userId())) {
            throw new ConflictException("User with id " + requestBody.userId() + " is already assigned to this ticket");
        }

        List<Assignee> assignees = repository.addAssigneeToTicket(ticketId, requestBody.userId());

        Ticket ticket = repository.getTicketById(ticketId).orElseThrow();

        String warning = sendEmailOrGetWarning(assignees, ticket,
                "Assignee added successfully, but email notification could not be sent.");

        return new PostTicketAssigneeResponse(ticketId, toAssigneeDtos(assignees), warning);
    }

    public DeleteTicketAssigneeResponse removeAssigneeFromTicket(int ticketId, int userId){
        repository.getTicketById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket with id " + ticketId + " doesn't exist"));


        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist"));


        if (!repository.isUserAssigned(ticketId, userId)) {
            throw new NotFoundException("User is not assigned to this ticket");
        }

        List<Assignee> assignees = repository.deleteAssigneeFromTicket(ticketId, userId);

        Ticket ticket = repository.getTicketById(ticketId).orElseThrow();

        String warning = sendEmailOrGetWarning(assignees, ticket,
                "Assignee removed successfully, but email notification could not be sent.");

        return new DeleteTicketAssigneeResponse(ticketId, toAssigneeDtos(assignees), warning);

    }

    private GetTicketResponse toGetTicketResponse(Ticket ticket){
        return new GetTicketResponse(
                ticket.getTicketId(),
                ticket.getTicketTitle(),
                ticket.getTicketDescription(),
                ticket.getProjectId(),
                ticket.getTicketStatus(),
                ticket.getTicketCreatedAt(),
                ticket.getTicketUpdatedAt(),
                toAssigneeDtos(ticket.getAssignees())
        );
    }

    private List<GetTicketAssigneesInfoResponse> toAssigneeDtos(List<Assignee> assignees) {
        return assignees
            .stream()
            .map(a -> new GetTicketAssigneesInfoResponse(a.getUserId(), a.getUserName()))
            .toList();
    }

    private String sendEmailOrGetWarning(List<Assignee> assignees, Ticket ticket, String warningText){
        List<String> emails = assignees.stream().map(Assignee::getUserEmail).toList();
        List<String> names = assignees.stream().map(Assignee::getUserName).toList();

        String result = resendService.sendTicketUpdateNotification(
                ticket.getTicketId(),
                ticket.getTicketTitle(),
                ticket.getTicketStatus(),
                emails,
                names
        );

        return result != null ? warningText : null;
    }
}
