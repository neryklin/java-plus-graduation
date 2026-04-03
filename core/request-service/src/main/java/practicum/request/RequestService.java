package practicum.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import practicum.interaction.exception.GlobalExceptionHandler;

@SpringBootApplication
@EnableFeignClients
@Import(GlobalExceptionHandler.class)
public class RequestService {
    public static void main(String[] args) {

        SpringApplication.run(RequestService.class, args);
    }

}
