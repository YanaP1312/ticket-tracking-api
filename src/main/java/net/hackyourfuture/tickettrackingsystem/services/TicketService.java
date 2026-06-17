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
import net.hackyourfuture.tickettrackingsystem.repositories.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final  TicketRepository repository;
    private final  ProjectService projectService;
    private final UserService userService;
    private final ResendService resendService;

    public TicketService(TicketRepository repository, ProjectService projectService, UserService userService, ResendService resentService){
        this.repository = repository;
        this.projectService = projectService;
        this.userService = userService;
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
        projectService.getProjectById(requestBody.projectId());

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
        getTicketById(ticketId);

        boolean titleBlank = requestBody.ticketTitle() == null || requestBody.ticketTitle().isBlank();
        boolean descriptionBlank = requestBody.ticketDescription() == null || requestBody.ticketDescription().isBlank();
        boolean projectIdBlank = requestBody.projectId() == null;
        boolean statusBlank = requestBody.ticketStatus() == null || requestBody.ticketStatus().isBlank();

        if (titleBlank && descriptionBlank && projectIdBlank && statusBlank) {
            throw new BadRequestException("At least one field must be provided");
        }

        if (requestBody.projectId() != null) {
            projectService.getProjectById(requestBody.projectId());
        }

        PatchTicketRequest safeRequestBody = new PatchTicketRequest(
                titleBlank ? null : requestBody.ticketTitle(),
                requestBody.ticketDescription(),
                requestBody.projectId(),
                requestBody.ticketStatus()
        );

        Ticket ticket = repository.updateTicket(ticketId, safeRequestBody);

        String warning = sendEmail(ticket.getAssignees(), ticket)
                ? null
                : "Ticket updated successfully, but email notification could not be sent.";

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
        getTicketById(ticketId);

        userService.getUserById(requestBody.userId());

        if (repository.isUserAssigned(ticketId, requestBody.userId())) {
            throw new ConflictException("User with id " + requestBody.userId() + " is already assigned to this ticket");
        }

        List<Assignee> assignees = repository.addAssigneeToTicket(ticketId, requestBody.userId());

        Ticket ticket = repository.getTicketById(ticketId).orElseThrow();

        String warning = sendEmail(assignees, ticket)
                ? null
                : "Assignee added successfully, but email notification could not be sent.";

        return new PostTicketAssigneeResponse(ticketId, toAssigneeDtos(assignees), warning);
    }

    public DeleteTicketAssigneeResponse removeAssigneeFromTicket(int ticketId, int userId){
        getTicketById(ticketId);

        userService.getUserById(userId);

        if (!repository.isUserAssigned(ticketId, userId)) {
            throw new NotFoundException("User is not assigned to this ticket");
        }

        List<Assignee> assignees = repository.deleteAssigneeFromTicket(ticketId, userId);

        Ticket ticket = repository.getTicketById(ticketId).orElseThrow();

        String warning = sendEmail(assignees, ticket)
                ? null
                : "Assignee removed successfully, but email notification could not be sent.";

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

    private boolean sendEmail(List<Assignee> assignees, Ticket ticket) {
        List<String> emails = assignees.stream().map(Assignee::getUserEmail).toList();
        List<String> names = assignees.stream().map(Assignee::getUserName).toList();

        return resendService.sendTicketUpdatedNotification(
                ticket.getTicketId(),
                ticket.getTicketTitle(),
                ticket.getTicketStatus(),
                emails,
                names
        );
    }
}
