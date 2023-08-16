package com.example.kafkaconsumer;

import com.example.kafkaconsumer.service.EventServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MyKafkaListener {

    @Autowired
    EventServiceImpl eventService;

    ObjectMapper objectMapper = new ObjectMapper();

    @RetryableTopic(
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 3000, multiplier = 2.0),
            exclude = {SerializationException.class, DeserializationException.class},
            autoCreateTopics = "true")
    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${topic}", containerFactory = "myListenerContainerFactory")
    public void listen(@Headers MessageHeaders headers, @Payload GenericRecord record) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        log.info("Headers: {}", headers);
        log.info("RECEIVED_KEY: {}", headers.get(KafkaHeaders.RECEIVED_KEY));
        log.info("RECEIVED_TOPIC: {}", headers.get(KafkaHeaders.RECEIVED_TOPIC));
        log.info("Payload: {}", record);
        var response = eventService.appendRecord(record);
        log.info("response: {}", response);
    }

    @DltHandler
    public void handleDlt(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, GenericRecord message) {
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }

}
