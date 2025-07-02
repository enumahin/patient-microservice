package com.alienworkspace.cdr.patient.service.client;

import com.alienworkspace.cdr.model.dto.person.PersonDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.model.helper.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback class for the demographic service.
 */
@Component
public class DemographicFallback implements DemographicFeignClient {

    /**
     * Gets a person by id.
     *
     * @param id The id of the person.
     * @return The person.
     */
    @Override
    public ResponseEntity<PersonDto> getPerson(String correlationId, long id, boolean includeVoided) {
        return ResponseEntity.ok(null);
    }

    /**
     * Adds a person.
     *
     * @param personDto The person to add.
     * @return The added person.
     */
    @Override
    public ResponseEntity<PersonDto> addPerson(String correlationId, PersonDto personDto) {
        return ResponseEntity.ok(null);
    }

    /**
     * Deletes a person.
     *
     * @param id The id of the person.
     * @return The deleted person.
     */
    @Override
    public ResponseEntity<ResponseDto> deletePerson(long id, RecordVoidRequest voidRequest) {
        return ResponseEntity.ok(null);
    }
}
