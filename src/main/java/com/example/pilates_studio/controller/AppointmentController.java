package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.AppointmentDto;
import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.model.Appointment;
import com.example.pilates_studio.model.AppointmentStatus;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AppointmentController {

    private AppointmentService appointmentService;
    private CustomerService customerService;
    private TrainerService trainerService;
    private LocationService locationService;
    private PurchaseService purchaseService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, CustomerService customerService, TrainerService trainerService, LocationService locationService, PurchaseService purchaseService){
        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.trainerService = trainerService;
        this.locationService = locationService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/appointments")
    public String listAppointments(@RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate, Model model){

        List<AppointmentDto> appointments;
        if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()){
            appointments = appointmentService.findAppointmentsWithinDateRange(startDate, endDate);
        }else{
            appointments = appointmentService.findAllAppointments();
        }
        model.addAttribute("appointments", appointments);
        return "appointments-list";
    }

    @GetMapping("/appointments/new")
    public String createAppointmentForm(Model model){
        Appointment appointment = new Appointment();
        model.addAttribute("appointment", appointment);
        model.addAttribute("customers", customerService.findAllCustomers());
        model.addAttribute("trainers", trainerService.findAllTrainers());
        model.addAttribute("locations", locationService.findAllLocations());
        return "appointments-create";
    }

    @PostMapping("/appointments/new")
    public String saveAppointment(@Valid @ModelAttribute("appointment") Appointment appointment, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            model.addAttribute("appointment", appointment);
            model.addAttribute("customers", customerService.findAllCustomers());
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-create";
        }

        CustomerDto customer = customerService.findCustomerById(appointment.getCustomer().getId());
        if(customer.getRemainingUsage() <= 0){
            result.rejectValue("customer.id", "error.appointment", "This customer has no remaining lessons");
            model.addAttribute("customers", customerService.findAllCustomers());
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-create";
        }

        if(appointment.getStartTime().isAfter(appointment.getEndTime())){
            result.rejectValue("startTime", "error.appointment", "Start time must be before end time");
            model.addAttribute("customers", customerService.findAllCustomers());
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-create";
        }

        if (!appointmentService.isSlotAvailable(appointment.getLocationId(),
                appointment.getStartTime(),
                appointment.getEndTime())) {
            result.rejectValue("locationId", "error.appointment", "The selected room is not available at this time.");
            model.addAttribute("customers", customerService.findAllCustomers());
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-create";
        }

        appointment.setCustomer(customerService.findCustomerModelById(appointment.getCustomer().getId()));
        customerService.decrementUsage(appointment.getCustomer().getId());
        appointment.setPurchaseId(purchaseService.findInUsePurchaseIdByCustomerId(appointment.getCustomer().getId()));
        if(appointment.getCustomer().getRemainingUsage() == 0){
            purchaseService.setStatusAsUsed(appointment.getCustomer());
        }
        if(appointment.getTrainer().getId() == null){
            appointment.setAppointmentStatus(AppointmentStatus.PENDING_TRAINER);
            appointment.setTrainer(null);
        }else{
            appointment.setAppointmentStatus(AppointmentStatus.SCHEDULED);
        }

        appointmentService.saveAppointment(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointments/{appointmentId}/edit")
    public String editPurchaseForm(@PathVariable("appointmentId") Integer appointmentId, Model model){
        AppointmentDto appointment = appointmentService.findAppointmentById(appointmentId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedStartTime = appointment.getStartTime().format(formatter);
        String formattedEndTime = appointment.getEndTime().format(formatter);
        model.addAttribute("formattedStartTime", formattedStartTime);
        model.addAttribute("formattedEndTime", formattedEndTime);
        model.addAttribute("appointment", appointment);
        model.addAttribute("trainers", trainerService.findAllTrainers());
        model.addAttribute("locations", locationService.findAllLocations());
        return "appointments-edit";
    }

    @PostMapping("/appointments/{appointmentId}/edit")
    public String updateAppointment(@PathVariable("appointmentId") Integer appointmentId, @Valid @ModelAttribute("appointment") AppointmentDto appointmentDto, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-edit";
        }

        if (appointmentDto.getStartTime().isAfter(appointmentDto.getEndTime())) {
            result.rejectValue("startTime", "error.appointment", "Start time must be before end time");
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-edit";
        }

        if (!appointmentService.isSlotAvailable(appointmentDto.getLocationId(), appointmentDto.getStartTime(), appointmentDto.getEndTime())) {
            result.rejectValue("locationId", "error.appointment", "The selected room is not available at this time.");
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-edit";
        }

        AppointmentDto appointment = appointmentService.findAppointmentById(appointmentId);
        appointmentDto.setCustomerName(appointment.getCustomerName());
        appointmentService.updateAppointment(appointmentDto);

        return "redirect:/appointments";
    }
}
