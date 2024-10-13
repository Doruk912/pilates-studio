package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.LocationDto;
import com.example.pilates_studio.model.Location;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationService {
    List<LocationDto> findAllLocations();
    Location saveLocation(LocationDto locationDto);
    LocationDto findLocationById(String locationId);
    void delete(String locationId);
    List<String> getRoomsByFranchise(String franchise);

    List<String> findAllFranchises();
}
