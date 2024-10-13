package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.AppointmentDto;
import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.dto.PurchaseDto;
import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.Purchase;
import com.example.pilates_studio.service.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private AppointmentService appointmentService;
    private CustomerService customerService;
    private PurchaseService purchaseService;
    private TrainerService trainerService;
    private LocationService locationService;
    private PdfService pdfService;

    public DashboardController(AppointmentService appointmentService, CustomerService customerService, PurchaseService purchaseService, TrainerService trainerService, LocationService locationService, PdfService pdfService){
        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.purchaseService = purchaseService;
        this.trainerService = trainerService;
        this.locationService = locationService;
        this.pdfService = pdfService;
    }
    @GetMapping(value = {"/", "dashboard"})
    public String homePage(Model model) {
        int activeAppointments = appointmentService.countActiveAppointments();
        int pendingTrainerAppointments = appointmentService.countPendingTrainerAppointments();
        int totalCustomers = customerService.countTotalCustomers();
        int purchasesInUse = purchaseService.countPurchasesInUse();

        List<String> franchises = locationService.findAllFranchises();
        List<TrainerDto> trainers = trainerService.findAllTrainers();
        List<CustomerDto> customers = customerService.findAllCustomers();
        List<AppointmentDto> appointments = appointmentService.findAllAppointments();
        model.addAttribute("activeAppointments", activeAppointments);
        model.addAttribute("pendingTrainerAppointments", pendingTrainerAppointments);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("purchasesInUse", purchasesInUse);
        model.addAttribute("franchises", franchises);
        model.addAttribute("trainers", trainers);
        model.addAttribute("customers", customers);
        model.addAttribute("appointments", appointments);

        return "dashboard";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<String> getRooms(@RequestParam String franchise){
        return locationService.getRoomsByFranchise(franchise);
    }

    @GetMapping("/downloadRoomSchedule")
    public ResponseEntity<InputStreamResource> downloadRoomSchedule(@RequestParam("franchise") String franchise, @RequestParam("room") String room, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        ByteArrayInputStream pdf = pdfService.generateRoomSchedulePdf(franchise, room, date);

        HttpHeaders headers = new HttpHeaders();
        String filename = franchise + "-" + room + " " + date + " Schedule" + ".pdf";
        headers.add("Content-Disposition", "inline; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

    @GetMapping("/downloadTrainerSchedule")
    public ResponseEntity<InputStreamResource> downloadTrainerSchedule(@RequestParam("trainer") String trainer, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        ByteArrayInputStream pdf = pdfService.generateTrainerSchedulePdf(trainer, date);

        HttpHeaders headers = new HttpHeaders();
        String filename = trainerService.findTrainerById(Integer.valueOf(trainer)).getName() + " " + date + " Schedule" + ".pdf";
        headers.add("Content-Disposition", "inline; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

    @GetMapping("/downloadCustomerPackageDetails")
    public ResponseEntity<InputStreamResource> downloadCustomerPackageDetails(
            @RequestParam(value = "customer", required = false) String customer,
            @RequestParam(value = "purchase", required = false) Integer purchase) {

        ByteArrayInputStream pdf;
        String filename;

        if(purchase == null){
            pdf = pdfService.generateCustomerPackageDetailsPdf(Integer.valueOf(customer));
            filename = customerService.findCustomerById(Integer.valueOf(customer)).getName() + " Package Details" + ".pdf";
        }else{
            pdf = pdfService.generateCustomerPackageDetailsPdfByPurchaseId(purchase);
            filename = "Purchase ID " + purchase + " Package Details" + ".pdf";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }


}
