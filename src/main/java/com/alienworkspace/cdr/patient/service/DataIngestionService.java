package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@AllArgsConstructor
@Service
public class DataIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DataIngestionService.class);

    private final ObjectMapper objectMapper;

    private final PatientService patientService;

    @Bean
    public Consumer<PatientDto> patientConsumer() {
        return patientDto -> {
            log.info("Consuming patientDto: {}", patientDto);
            patientService.createPatient(patientDto);
        };
    }
}
