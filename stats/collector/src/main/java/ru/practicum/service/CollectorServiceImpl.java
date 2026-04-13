package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorServiceImpl implements CollectorService {
    private final Producer<String, UserActionAvro> producer;

    @Override
    public void sendActionFromKafka(UserActionAvro userAction) {
        ProducerRecord<String, UserActionAvro> record = new ProducerRecord<>("stats.user-actions.v1", userAction);
        log.info("sending record: {} \n", record);
        producer.send(record);
        log.info("send - OK: {} \n", record);
        producer.flush();
        log.info("flush after send  - OK: {} \n", record);
    }
}