package net.hackyourfuture.tickettrackingsystem.services;

import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketAssigneeRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.users.GetUserResponse;
import net.hackyourfuture.tickettrackingsystem.exceptions.ConflictException;
import net.hackyourfuture.tickettrackingsystem.exceptions.NotFoundException;
import net.hackyourfuture.tickettrackingsystem.models.Ticket;
import net.hackyourfuture.tickettrackingsystem.repositories.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @InjectMocks
    private TicketService service;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserService userService;


    @Test
    void getTicketById_whenTicketNotFound_throwsNotFoundException() {
        // Arrange
        when(ticketRepository.getTicketById(999))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> service.getTicketById(999));
    }

    @Test
    void createTicket_whenProjectNotFound_throwsNotFoundException() {
        // Arrange
        PostTicketRequest request = new PostTicketRequest(
                "title",
                "description",
                999,
                "open"
        );

        when(projectService.getProjectById(999))
                .thenThrow(new NotFoundException("Project with id 999 not found"));
        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> service.createTicket(request));
    }

    @Test
    void addAssigneeToTicket_whenUserAlreadyAssigned_throwsConflictException() {
        // Arrange
        Ticket ticket = new Ticket(1, "title", "description", 1, "open",
                LocalDateTime.now(), null, new ArrayList<>());

        PostTicketAssigneeRequest request = new PostTicketAssigneeRequest(2);

        when(ticketRepository.getTicketById(1))
                .thenReturn(Optional.of(ticket));
        when(userService.getUserById(2))
                .thenReturn(new GetUserResponse(2, "name", "email"));
        when(ticketRepository.isUserAssigned(1, 2))
                .thenReturn(true);
        // Act & Assert
        assertThrows(ConflictException.class,
                () -> service.addAssigneeToTicket(1, request));
    }
}