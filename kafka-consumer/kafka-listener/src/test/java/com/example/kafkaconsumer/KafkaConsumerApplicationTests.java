package com.example.kafkaconsumer;

import com.example.avro.MyRecord;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;
import java.util.HashMap;

class KafkaConsumerApplicationTests {

//	@Test
//	void contextLoads() {
//	}

    MyKafkaListener myKafkaListener = new MyKafkaListener();

    Message<MyRecord> message = new Message<MyRecord>() {
        @Override
        public MyRecord getPayload() {
            return MyRecord.newBuilder().setName("Name").build();
        }

        @Override
        public MessageHeaders getHeaders() {
            return new MessageHeaders(new HashMap<>() {{
                put("correlationId", "1");
            }});
        }
    };


    @Test
    void testListen() throws IOException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        myKafkaListener.listen(message.getHeaders(), message.getPayload());
    }

    @Test
    void testDlt() {
        myKafkaListener.handleDlt("test-topic", message.getPayload());
    }


}
