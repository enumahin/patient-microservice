package com.alienworkspace.cdr.patient.service.client;


import static com.alienworkspace.cdr.patient.helpers.Constants.DEMOGRAPHIC_BASE_URL;

import com.alienworkspace.cdr.model.dto.person.PersonDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.model.helper.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for interacting with the demographic service.
 */
@FeignClient("demographic")
public interface DemographicFeignClient {

    /**
     * Gets a person by id.
     *
     * @param id The id of the person.
     * @return The person.
     */
    @GetMapping(DEMOGRAPHIC_BASE_URL + "/people/{id}/{includeVoided}")
    ResponseEntity<PersonDto> getPerson(@PathVariable("id") long id,
                                        @PathVariable("includeVoided") boolean includeVoided);

    /**
     * Adds a person.
     *
     * @param personDto The person to add.
     * @return The added person.
     */
    @PostMapping(DEMOGRAPHIC_BASE_URL + "/people")
    ResponseEntity<PersonDto> addPerson(@Valid @RequestBody PersonDto personDto);

    /**
     * Deletes a person.
     *
     * @param id The id of the person.
     * @return The deleted person.
     */
    @DeleteMapping(DEMOGRAPHIC_BASE_URL + "/people/{id}")
    ResponseEntity<ResponseDto> deletePerson(@PathVariable("id") long id, @RequestBody RecordVoidRequest voidRequest);
}
