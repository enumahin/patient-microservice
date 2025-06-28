package com.alienworkspace.cdr.patient.service.client;

import com.alienworkspace.cdr.model.dto.metadata.CityDto;
import com.alienworkspace.cdr.model.dto.metadata.CommunityDto;
import com.alienworkspace.cdr.model.dto.metadata.CountryDto;
import com.alienworkspace.cdr.model.dto.metadata.CountyDto;
import com.alienworkspace.cdr.model.dto.metadata.LocationDto;
import com.alienworkspace.cdr.model.dto.metadata.StateDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for interacting with the metadata service.
 */
@FeignClient("metadata")
public interface MetadataFeignClient {

    /**
     * Gets all countries.
     *
     * @return The list of countries.
     */
    @GetMapping
    ResponseEntity<List<CountryDto>> getAllCountries();

    /**
     * Gets a country by id.
     *
     * @param id The id of the country.
     * @return The country.
     */
    @GetMapping("/{id}")
    ResponseEntity<CountryDto> getCountry(@PathVariable int id);

    /**
     * Gets a location by id.
     *
     * @param countryId The id of the country.
     * @param stateId The id of the state.
     * @param countyId The id of the county.
     * @param cityId The id of the city.
     * @param communityId The id of the community.
     * @param locationId The id of the location.
     * @return The location.
     */
    @GetMapping("/{countryId}/{stateId}/{countyId}/{cityId}/{communityId}/{locationId}")
    ResponseEntity<CountryDto> getPersonLocation(@PathVariable int countryId, @PathVariable Integer stateId,
                                                 @PathVariable Integer countyId, @PathVariable Integer cityId,
                                                 @PathVariable Integer communityId, @PathVariable Integer locationId);

    /**
     * Gets all states.
     *
     * @return The list of states.
     */
    @GetMapping
    ResponseEntity<List<StateDto>> getAllStates();

    /**
     * Gets a state by id.
     *
     * @param id The id of the state.
     * @return The state.
     */
    @GetMapping("/{id}")
    ResponseEntity<StateDto> getState(@PathVariable int id);

    /**
     * Gets all counties.
     *
     * @return The list of counties.
     */
    @GetMapping
    ResponseEntity<List<CountyDto>> getAllCounties();

    /**
     * Gets a county by id.
     *
     * @param id The id of the county.
     * @return The county.
     */
    @GetMapping("/{id}")
    ResponseEntity<CountyDto> getCounty(@RequestHeader("X-cdr-correlation-id") String correlationId,
                                        @PathVariable int id);

    /**
     * Gets all cities.
     *
     * @return The list of cities.
     */
    @GetMapping
    ResponseEntity<List<CityDto>> getAllCities();

    /**
     * Gets a city by id.
     *
     * @param id The id of the city.
     * @return The city.
     */
    @GetMapping("/{id}")
    ResponseEntity<CityDto> getCity(@PathVariable int id);

    /**
     * Gets all communities.
     *
     * @return The list of communities.
     */
    @GetMapping
    ResponseEntity<List<CommunityDto>> getAllCommunities();

    /**
     * Gets a community by id.
     *
     * @param id The id of the community.
     * @return The community.
     */
    @GetMapping("/{id}")
    ResponseEntity<CommunityDto> getCommunity(@PathVariable int id);

    /**
     * Gets all locations.
     *
     * @return The list of locations.
     */
    @GetMapping
    ResponseEntity<List<LocationDto>> getAllLocations();

    /**
     * Gets a location by id.
     *
     * @param id The id of the location.
     * @return The location.
     */
    @GetMapping("/{id}")
    ResponseEntity<LocationDto> getLocation(@PathVariable int id);
}
