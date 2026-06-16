package net.hackyourfuture.tickettrackingsystem.config.client;

import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ResendApiConfiguration {

    @Value("${resend.api.key}")
    public String apiKey;

    @Bean
    public Resend resend(){
        return new Resend(apiKey);
    }
}
