package net.hackyourfuture.tickettrackingsystem.controllers;

import net.hackyourfuture.tickettrackingsystem.exceptions.BadRequestException;
import net.hackyourfuture.tickettrackingsystem.services.ResendService;
import net.hackyourfuture.tickettrackingsystem.services.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TicketController.class)
class TicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private ResendService resendService;

    @Test
    void getAllTickets_returns200WithList() throws Exception{
        //Arrange
        when(ticketService.getAllTickets(null, null))
                .thenReturn(List.of());
        //Act & Assert
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    void getAllTickets_withValidStatus_return200() throws Exception{
        //Arrange
        when(ticketService.getAllTickets(null, "in_progress"))
                .thenReturn(List.of());

        //Act & Assert
        mockMvc.perform(get("/tickets?status=in_progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    void getAllTickets_withInvalidStatus_return400WithError() throws Exception{
        //Arrange
        when(ticketService.getAllTickets(null, "invalid"))
                .thenThrow(new BadRequestException("Status must be one of: open, in_progress, closed"));

        //Act & Assert
        mockMvc.perform(get("/tickets?status=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

    }

}