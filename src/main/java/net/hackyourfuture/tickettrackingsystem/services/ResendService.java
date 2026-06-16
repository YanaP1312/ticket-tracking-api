package net.hackyourfuture.tickettrackingsystem.services;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResendService {

    private final Resend resend;

    @Value("${resend.from}")
    private String from;

    public ResendService(Resend resend){
        this.resend = resend;
    }

    public String sendTicketUpdateNotification(
            int ticketId,
            String ticketTitle,
            String ticketStatus,
            List<String> assigneeEmails,
            List<String> assigneeNames
    ){
        if(assigneeEmails.isEmpty()){
            return null;
        }

        try{
            String html = buildEmailHtml(ticketId, ticketTitle, ticketStatus, assigneeNames);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(from)
                    .to(assigneeEmails)
                    .subject("Ticket #" + ticketId + " updated")
                    .html(html)
                    .build();

            resend.emails().send(params);
            return null;
        } catch (Exception e){
            return "warning";
        }
    }

    private String buildEmailHtml(int ticketId, String ticketTitle, String ticketStatus, List<String> assigneeNames){
        return """
                <p>Ticket #%d updated:</p>
                <p>Title: "%s"</p>
                <p>Status: "%s"</p>
                <p>Current assignees: %s</p>
                """.formatted(ticketId, ticketTitle, ticketStatus, String.join(", ", assigneeNames));
    }
}
