package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.AppointmentDto;
import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.*;
import com.example.pilates_studio.repository.AppointmentRepository;
import com.example.pilates_studio.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private AppointmentRepository appointmentRepository;
    private CustomerService customerService;
    private TrainerService trainerService;
    private PurchaseRepository purchaseRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, CustomerService customerService, TrainerService trainerService, PurchaseRepository purchaseRepository){
        this.appointmentRepository = appointmentRepository;
        this.customerService = customerService;
        this.trainerService = trainerService;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public List<AppointmentDto> findAllAppointments() {
        return appointmentRepository.findAllAppointmentsWithCustomerAndTrainerNames()
                .stream()
                .map(this::mapToAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Override
    public AppointmentDto findAppointmentById(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        return mapToAppointmentDto(appointment);
    }

    @Override
    public Appointment findAppointment(Integer appointmentId) {
        return appointmentRepository.findById(appointmentId).get();
    }

    @Override
    public String findCustomerNameFromAppointmentId(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        return appointment.getCustomer().getName();

    }

    @Override
    public void updateAppointment(AppointmentDto appointmentDto) {
        Appointment appointment = mapToAppointment(appointmentDto);
        appointmentRepository.save(appointment);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Override
    public boolean isSlotAvailable(String locationId, LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.isSlotAvailable(locationId, startTime, endTime);
    }

    @Override
    public List<AppointmentDto> findAppointmentsWithinDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter).plusDays(1);
        List<Appointment> appointments = appointmentRepository.findAppointmentsWithinDateRange(start.atStartOfDay(), end.atStartOfDay());
        return appointments.stream().map(this::mapToAppointmentDto).collect(Collectors.toList());
    }

    @Override
    public int countActiveAppointments() {
        List<AppointmentStatus> statuses = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.PENDING_TRAINER);
        return appointmentRepository.countByAppointmentStatusIn(statuses);
    }

    @Override
    public int countPendingTrainerAppointments() {
        return appointmentRepository.countByAppointmentStatus(AppointmentStatus.PENDING_TRAINER);
    }

    @Override
    public List<AppointmentDto> findAppointmentsByFranchiseAmdRoomAndDate(String franchise, String room, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<Appointment> appointments = appointmentRepository.findAppointmentsByFranchiseAndRoomAndDate(franchise, room, start, end);
        return appointments.stream().map(this::mapToAppointmentDto).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findAppointmentsByTrainerIdAndDate(String trainerId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsByTrainerIdAndDate(Integer.valueOf(trainerId), date.atStartOfDay(), date.atTime(23, 59, 59));
        return appointments.stream().map(this::mapToAppointmentDto).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findPackageAppointmentsByCustomerId(Integer customerId) {
        Purchase purchase = purchaseRepository.findByCustomerIdAndPackageStatus(customerId, PackageStatus.IN_USE);

        if(purchase == null){
            return Collections.emptyList();
        }

        List<Appointment> appointments = appointmentRepository.findAppointmentsByCustomerIdAndPurchaseIdOrderByStartTime(customerId, purchase.getId());

        return appointments.stream()
                .map(this::mapToAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findPackageAppointmentsByPurchaseId(Integer purchaseId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsByPurchaseIdOrderByStartTime(purchaseId);
        return appointments.stream()
                .map(this::mapToAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    @Override
    public void updateStatus(Integer id, AppointmentStatus appointmentStatus) {
        Appointment appointment = appointmentRepository.findById(id).get();
        appointment.setAppointmentStatus(appointmentStatus);
        appointmentRepository.save(appointment);
    }

    @Override
    public void assignTrainer(Integer appointmentId, Integer trainerId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        Trainer trainer = trainerService.findTrainer(trainerId);
        appointment.setTrainer(trainer);
        appointmentRepository.save(appointment);
    }

    @Override
    public Appointment findAppointmentModelById(Integer id) {
        return appointmentRepository.findById(id).get();
    }

    private Appointment mapToAppointment(AppointmentDto appointmentDto) {
        Customer customer = customerService.findCustomerByName(appointmentDto.getCustomerName());
        Trainer trainer = trainerService.findTrainerByName(appointmentDto.getTrainerName());
        return Appointment.builder()
                .id(appointmentDto.getId())
                .startTime(appointmentDto.getStartTime())
                .endTime(appointmentDto.getEndTime())
                .customer(customer)
                .trainer(trainer)
                .locationId(appointmentDto.getLocationId())
                .appointmentStatus(appointmentDto.getAppointmentStatus())
                .description(appointmentDto.getDescription())
                .build();
    }


    private AppointmentDto mapToAppointmentDto(Appointment appointment){
        return AppointmentDto.builder()
                .id(appointment.getId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .customerName(appointment.getCustomer().getName())
                .trainerName(appointment.getTrainer() != null ? appointment.getTrainer().getName() : "-")
                .locationId(appointment.getLocationId())
                .appointmentStatus(appointment.getAppointmentStatus())
                .description(appointment.getDescription())
                .build();
    }
}
