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

    public boolean sendTicketUpdatedNotification(
            int ticketId,
            String ticketTitle,
            String ticketStatus,
            List<String> assigneeEmails,
            List<String> assigneeNames
    ){
        if(assigneeEmails.isEmpty()){
            return true;
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
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private String buildEmailHtml(int ticketId, String ticketTitle, String ticketStatus, List<String> assigneeNames){
        String badgeColor = switch (ticketStatus) {
            case "open" -> "background: #e6f1fb; color: #0c447c;";
            case "in progress" -> "background: #faeeda; color: #633806;";
            case "closed" -> "background: #eaf3de; color: #27500a;";
            default -> "background: #f1efe8; color: #444441;";
        };

        return """
            <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #2c3e50; margin-top: 0; font-size: 20px;">Ticket #%d updated</h2>
                <p style="margin: 12px 0; font-size: 15px;"><span style="color: #888;">Title:</span> <strong>%s</strong></p>
                <p style="margin: 12px 0; font-size: 15px;">
                    <span style="color: #888;">Status:</span>
                    <span style="%s padding: 4px 10px; border-radius: 12px; font-size: 13px;">%s</span>
                </p>
                <p style="margin: 12px 0; font-size: 15px;"><span style="color: #888;">Current assignees:</span> <strong>%s</strong></p>
            </div>
            """.formatted(ticketId, ticketTitle, badgeColor, ticketStatus, String.join(", ", assigneeNames));
    }
}
