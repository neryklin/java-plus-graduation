package practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import practicum.interaction.exception.GlobalExceptionHandler;

@SpringBootApplication
@EnableFeignClients
@Import(GlobalExceptionHandler.class)
public class EventService {
    public static void main(String[] args) {

        SpringApplication.run(EventService.class, args);
    }

}