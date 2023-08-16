package com.example.kafkaconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class EventServiceImpl {
    @Autowired private EntityManager entityManager;

    @Value("${store.repository}")
    private String repositoryClassName;

    @Value("${entity.classname}")
    private String entityClassName;

    @Value("${entity.key.fieldname}")
    private String keyFieldName;

    ObjectMapper objectMapper = new ObjectMapper();

    public Object appendRecord(GenericRecord record) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName(entityClassName);
        var userData = objectMapper.readValue(record.toString().getBytes(StandardCharsets.UTF_8), clazz);
        Class<?> repositoryClazz = Class.forName(repositoryClassName);
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        var repository = (JpaRepository) factory.getRepository(repositoryClazz);
//        Optional optional = repository.findById(clazz.getField(keyFieldName));
//        if(optional.isPresent()){
//            var rec = optional.get();
//            repository.save(userData);
//        }else{
//            repository.save()
//        }
        var savedRecord = repository.save(userData);
        Field field = savedRecord.getClass().getDeclaredField(keyFieldName);
        field.setAccessible(true);
        log.info("{}: {}", keyFieldName, field.get(savedRecord));
        return field.get(savedRecord);
    }
}
