package com.example.kafkaconsumer.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public ConsumerFactory<String, GenericRecord> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, false);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return new DefaultKafkaConsumerFactory(props, new StringDeserializer(), new KafkaAvroDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GenericRecord>
    myListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
//        final DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
//            // 1 second pause, unlimited retries - allow the discard logic to deal with the limit.
//        }, new FixedBackOff(1000L, 3));
//        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    RecordInterceptor<String, GenericRecord> interceptor(ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory) {
        RecordInterceptor<String, GenericRecord> inter = new RecordInterceptor<String, GenericRecord>() {

            @Override
            public ConsumerRecord<String, GenericRecord> intercept(ConsumerRecord<String, GenericRecord> consumerRecord, Consumer<String, GenericRecord> consumer) {
                log.info("Intercepted record: {}", consumerRecord);
                return consumerRecord;
            }

            @Override
            public void failure(ConsumerRecord<String, GenericRecord> record, Exception exception,
                                Consumer<String, GenericRecord> consumer) {

                log.error("Record failed {} Exception: {}", record.value(), exception);
            }

        };
        factory.setRecordInterceptor(inter);
        return inter;
    }

//    @Bean
//    public DefaultErrorHandler errorHandler() {
//        BackOff fixedBackOff = new FixedBackOff(1000L, 1);
//        return new DefaultErrorHandler((consumerRecord, e) -> {}, fixedBackOff);
//    }
}
