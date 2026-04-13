package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.service.eventSimilarity.EventSimilarityService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityProcessor implements Runnable {
    private final Consumer<String, EventSimilarityAvro> consumer;
    private final EventSimilarityService eventSimilarityService;


    @Value("${kafka.topics.events-similarity}")
    private String topic;

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) continue;
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    eventSimilarityService.saveSimilarity(record.value());
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
