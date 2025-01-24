package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.AppointmentDto;
import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.Appointment;
import com.example.pilates_studio.model.AppointmentStatus;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.Trainer;
import com.example.pilates_studio.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        List<TrainerDto> trainers = trainerService.findAllTrainers();
        if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()){
            appointments = appointmentService.findAppointmentsWithinDateRange(startDate, endDate);
        }else{
            appointments = appointmentService.findAllAppointments();
        }
        model.addAttribute("appointments", appointments);
        model.addAttribute("trainers", trainers);
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
    public String saveAppointment(@RequestParam("startDate") String startDate, @RequestParam("startHour") String startHour, @Valid @ModelAttribute("appointment") Appointment appointment, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            model.addAttribute("appointment", appointment);
            model.addAttribute("customers", customerService.findAllCustomers());
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-create";
        }

        try{
            LocalDateTime startTime = LocalDateTime.parse(startDate + "T" + startHour);
            appointment.setStartTime(startTime);
            appointment.setEndTime(startTime.plusMinutes(59));
        } catch (DateTimeParseException e){
            result.rejectValue("startTime", "error.appointment", "Invalid date format");
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
        model.addAttribute("appointment", appointment);
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("startDate", appointment.getStartTime().toLocalDate());
        model.addAttribute("startHour", appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        return "appointments-edit";
    }

    @PostMapping("/appointments/{appointmentId}/edit")
    public String updateAppointment(@PathVariable("appointmentId") Integer appointmentId, @RequestParam("startDate") String startDate, @RequestParam("startHour") String startHour, @Valid @ModelAttribute("appointment") AppointmentDto appointmentDto, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-edit";
        }

        Appointment appointment = appointmentService.findAppointment(appointmentId);

        try{
            LocalDateTime startTime = LocalDateTime.parse(startDate + "T" + startHour);
            appointment.setStartTime(startTime);
            appointment.setEndTime(startTime.plusMinutes(59));
        } catch (DateTimeParseException e){
            result.rejectValue("startTime", "error.appointment", "Invalid date format");
            model.addAttribute("customers", customerService.findAllCustomers());
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-create";
        }

        if (appointment.getStartTime().isAfter(appointment.getEndTime())) {
            result.rejectValue("startTime", "error.appointment", "Start time must be before end time");
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-edit";
        }

        if (!appointmentService.isSlotAvailable(appointment.getLocationId(), appointment.getStartTime(), appointment.getEndTime())) {
            result.rejectValue("locationId", "error.appointment", "The selected room is not available at this time.");
            model.addAttribute("trainers", trainerService.findAllTrainers());
            model.addAttribute("locations", locationService.findAllLocations());
            return "appointments-edit";
        }

        appointment.setDescription(appointmentDto.getDescription());
        appointment.setLocationId(appointmentDto.getLocationId());
        appointmentService.updateAppointment(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointments/{appointmentId}/delete")
    public String deleteAppointment(@PathVariable("appointmentId") Integer appointmentId, RedirectAttributes redirectAttributes){
        try{
            appointmentService.delete(appointmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment deleted successfully");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete appointment");
        }
        return "redirect:/appointments";
    }

    @GetMapping("/appointments/{id}/status/completed")
    public String markAsCompleted(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.CANCELLED){
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot mark a cancelled appointment as completed.");
            return "redirect:/appointments";
        }
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.NO_SHOW){
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot mark a no show appointment as completed.");
            return "redirect:/appointments";
        }
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.PENDING_TRAINER){
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot mark a pending trainer appointment as completed.");
            return "redirect:/appointments";
        }
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.COMPLETED){
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment is already marked as completed.");
            return "redirect:/appointments";
        }
        appointmentService.updateStatus(id, AppointmentStatus.COMPLETED);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment marked as completed.");
        return "redirect:/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/assign-trainer")
    public String assignTrainer(@PathVariable Integer appointmentId, @RequestParam Integer trainerId, RedirectAttributes redirectAttributes) {
        AppointmentDto appointment = appointmentService.findAppointmentById(appointmentId);
        if (appointment.getAppointmentStatus() == AppointmentStatus.NO_SHOW || appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot assign a trainer to an cancelled appointment");
            return "redirect:/appointments";
        }
        if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot assign a trainer to a completed appointment.");
            return "redirect:/appointments";
        }
        try {
            appointmentService.assignTrainer(appointmentId, trainerId);
            redirectAttributes.addFlashAttribute("successMessage", "Trainer assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to assign trainer: " + e.getMessage());
        }
        return "redirect:/appointments";
    }


    @GetMapping("/appointments/{id}/status/cancelled")
    public String markAsCancelled(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.CANCELLED){
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment is already marked as cancelled.");
            return "redirect:/appointments";
        }
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.COMPLETED){
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot mark a completed appointment as cancelled.");
            return "redirect:/appointments";
        }

        Appointment appointment = appointmentService.findAppointmentModelById(id);
        Customer customer = appointment.getCustomer();
        customerService.incrementUsage(customer.getId());
        appointmentService.updateStatus(id, AppointmentStatus.CANCELLED);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment marked as cancelled. Usage refunded to customer.");
        return "redirect:/appointments";
    }

    @GetMapping("/appointments/{id}/status/no-show")
    public String markAsNoShow(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.NO_SHOW){
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment is already marked as no show.");
            return "redirect:/appointments";
        }
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.COMPLETED){
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot mark a completed appointment as no show.");
            return "redirect:/appointments";
        }
        if(appointmentService.findAppointmentById(id).getAppointmentStatus() == AppointmentStatus.CANCELLED){
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot mark a cancelled appointment as no show.");
            return "redirect:/appointments";
        }
        appointmentService.updateStatus(id, AppointmentStatus.NO_SHOW);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment marked as no show.");
        return "redirect:/appointments";
    }
}
