package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.LocationDto;
import com.example.pilates_studio.model.Location;
import com.example.pilates_studio.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LocationController {
    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {this.locationService = locationService;}

    @GetMapping("/locations")
    public String listLocations(Model model){
        List<LocationDto> locations = locationService.findAllLocations();
        model.addAttribute("locations", locations);
        return "locations-list";
    }

    @GetMapping("/locations/new")
    public String createLocationForm(Model model){
        Location location = new Location();
        model.addAttribute("location", location);
        return "locations-create";
    }

    @PostMapping("/locations/new")
    public String saveLocation(@Valid @ModelAttribute("location") LocationDto locationDto, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            model.addAttribute("location", locationDto);
            return "locations-create";
        }
        locationDto.setId(locationDto.getFranchise() + "-" + locationDto.getRoom() + "-" + locationDto.getSlot());
        locationService.saveLocation(locationDto);
        redirectAttributes.addFlashAttribute("successMessage", "Location created successfully");
        return "redirect:/locations";
    }

    @GetMapping("/locations/{locationId}/delete")
    public String deleteLocation(@PathVariable("locationId") String locationId, RedirectAttributes redirectAttributes){
        try {
            locationService.delete(locationId);
            redirectAttributes.addFlashAttribute("successMessage", "Location deleted successfully");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete location. This may be due to an appointment using this location");
        }
        return "redirect:/locations";
    }




}
