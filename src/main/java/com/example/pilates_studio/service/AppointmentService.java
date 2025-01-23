package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.AppointmentDto;
import com.example.pilates_studio.model.Appointment;
import com.example.pilates_studio.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    List<AppointmentDto> findAllAppointments();

    void saveAppointment(Appointment appointment);

    AppointmentDto findAppointmentById(Integer appointmentId);

    Appointment findAppointment(Integer appointmentId);

    String findCustomerNameFromAppointmentId(Integer appointmentId);

    void updateAppointment(AppointmentDto appointmentDto);

    void updateAppointment(Appointment appointment);

    boolean isSlotAvailable(String locationId, LocalDateTime startTime, LocalDateTime endTime);

    List<AppointmentDto> findAppointmentsWithinDateRange(String startDate, String endDate);

    int countActiveAppointments();

    int countPendingTrainerAppointments();

    List<AppointmentDto> findAppointmentsByFranchiseAmdRoomAndDate(String franchise, String room, LocalDate date);

    List<AppointmentDto> findAppointmentsByTrainerIdAndDate(String trainerId, LocalDate date);

    List<AppointmentDto> findPackageAppointmentsByCustomerId(Integer customerId);

    List<AppointmentDto> findPackageAppointmentsByPurchaseId(Integer purchaseId);

    void delete(Integer appointmentId);

    void updateStatus(Integer id, AppointmentStatus appointmentStatus);

    void assignTrainer(Integer appointmentId, Integer trainerId);
}
