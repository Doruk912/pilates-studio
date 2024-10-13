package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.dto.LocationDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.Location;
import com.example.pilates_studio.repository.CustomerRepository;
import com.example.pilates_studio.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService{

    private LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {this.locationRepository = locationRepository;}

    @Override
    public List<LocationDto> findAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream().sorted(Comparator.comparing(Location::getId)).map((location) -> mapToLocationDto(location)).collect(Collectors.toList());
    }

    @Override
    public Location saveLocation(LocationDto locationDto) {
        Location location = mapToLocation(locationDto);
        return locationRepository.save(location);
    }

    @Override
    public LocationDto findLocationById(String locationId) {
        Location location = locationRepository.findById(locationId).get();
        return mapToLocationDto(location);
    }

    @Override
    public void delete(String locationId) {
        locationRepository.deleteById(locationId);
    }

    @Override
    public List<String> getRoomsByFranchise(String franchise) {
        List<Location> locations = locationRepository.findByFranchise(franchise);

        return locations.stream()
                .map(Location::getRoom)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllFranchises() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(Location::getFranchise)
                .distinct()
                .collect(Collectors.toList());
    }


    private Location mapToLocation(LocationDto location){
        return Location.builder()
                .id(location.getId())
                .franchise(location.getFranchise())
                .room(location.getRoom())
                .slot(location.getSlot())
                .build();
    }

    private LocationDto mapToLocationDto(Location location){
        return LocationDto.builder()
                .id(location.getId())
                .franchise(location.getFranchise())
                .room(location.getRoom())
                .slot(location.getSlot())
                .build();
    }
}
