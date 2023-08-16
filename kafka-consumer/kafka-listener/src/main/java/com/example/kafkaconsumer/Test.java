package com.example.kafkaconsumer;

import com.example.entity.UserData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserData data = objectMapper.readValue("{\"name\": \"record5\"}".getBytes(StandardCharsets.UTF_8), UserData.class);
        System.out.println(data);
    }
}
