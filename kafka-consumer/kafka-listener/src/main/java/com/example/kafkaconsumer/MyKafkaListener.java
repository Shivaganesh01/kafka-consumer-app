package com.example.kafkaconsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
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
import com.example.avro.MyRecord;

@Slf4j
@Component
public class MyKafkaListener {

    @RetryableTopic(
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 3000, multiplier = 2.0),
            exclude = {SerializationException.class, DeserializationException.class},
            autoCreateTopics = "true")
    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${topic}", containerFactory = "myListenerContainerFactory")
    public void listen(@Headers MessageHeaders headers, @Payload MyRecord record) {
        log.info("Headers: {}", headers);
        log.info("RECEIVED_KEY: {}", headers.get(KafkaHeaders.RECEIVED_KEY));
        log.info("RECEIVED_TOPIC: {}", headers.get(KafkaHeaders.RECEIVED_TOPIC));
        log.info("Payload: {}", record);
//        throw new NullPointerException("a");
    }

    @DltHandler
    public void handleDlt(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, MyRecord message) {
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }

}
