package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.userAction.UserActionService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor implements Runnable {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final Consumer<String, UserActionAvro> consumer;
    private final UserActionService service;
    @Value("${kafka.topics.user-actions}")
    private String topic;

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    service.saveUserAction(record.value());
                }
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("error sync offser {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {
            log.info("wakeuo");
        } catch (Exception e) {
            log.error("errors event sensor", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("close consumer");
                consumer.close();
            }
        }
    }
}
