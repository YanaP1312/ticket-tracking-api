package net.hackyourfuture.tickettrackingsystem.controllers;

import jakarta.validation.Valid;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PatchTicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketAssigneeRequest;
import net.hackyourfuture.tickettrackingsystem.dto.request.tickets.PostTicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.response.tickets.*;
import net.hackyourfuture.tickettrackingsystem.services.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service){
        this.service = service;
    }

    @GetMapping
    public List<GetTicketResponse> getAllTickets(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String status){
        return service.getAllTickets(text, status);
    }

    @GetMapping("/{id}")
    public GetTicketResponse getTicketById(
            @PathVariable int id){
        return service.getTicketById(id);
    }

    @PostMapping
    public PostTicketResponse createTicket(
            @Valid @RequestBody PostTicketRequest requestBody){
        return service.createTicket(requestBody);
    }

    @PatchMapping("/{id}")
    public PatchTicketResponse updateTicket(
            @PathVariable int id,
            @Valid @RequestBody PatchTicketRequest requestBody){
        return service.updateTicket(id, requestBody);
    }

    @PostMapping("/{id}/assignees")
    public PostTicketAssigneeResponse addAssigneeToTicket(
            @PathVariable int id,
            @Valid @RequestBody PostTicketAssigneeRequest requestBody){
        return service.addAssigneeToTicket(id, requestBody);
    }

    @DeleteMapping("/{id}/assignees/{userId}")
    public DeleteTicketAssigneeResponse removeAssigneeFromTicket(
            @PathVariable int id,
            @PathVariable int userId){
        return service.removeAssigneeFromTicket(id, userId);
    }
}
