package com.example.pilates_studio.repository;

import com.example.pilates_studio.model.Appointment;
import com.example.pilates_studio.model.AppointmentStatus;
import com.example.pilates_studio.model.PackageStatus;
import com.example.pilates_studio.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.customer c " +
            "LEFT JOIN FETCH a.trainer t " +
            "WHERE c.name IS NOT NULL " +
            "ORDER BY a.id DESC")
    List<Appointment> findAllAppointmentsWithCustomerAndTrainerNames();




    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN false ELSE true END " +
            "FROM Appointment a WHERE a.locationId = :locationId " +
            "AND a.appointmentStatus IN ('SCHEDULED', 'PENDING_TRAINER') " +
            "AND ((a.startTime <= :endTime AND a.endTime >= :startTime))")
    boolean isSlotAvailable(@Param("locationId") String locationId,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);


    @Query("SELECT a FROM Appointment a WHERE a.startTime >= :startDate AND a.endTime <= :endDate")
    List<Appointment> findAppointmentsWithinDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    int countByAppointmentStatusIn(Collection<AppointmentStatus> statuses);

    int countByAppointmentStatus(AppointmentStatus pendingTrainer);

    @Query("SELECT a FROM Appointment a WHERE a.locationId LIKE CONCAT(:franchise, '-%', :room, '-%') AND a.startTime BETWEEN :startOfDay AND :endOfDay ORDER BY a.startTime")
    List<Appointment> findAppointmentsByFranchiseAndRoomAndDate(@Param("franchise") String franchise, @Param("room") String room, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT a FROM Appointment a WHERE a.trainer.id = :trainerId AND a.startTime BETWEEN :startOfDay AND :endOfDay ORDER BY a.startTime")
    List<Appointment> findAppointmentsByTrainerIdAndDate(@Param("trainerId") Integer trainerId,
                                                         @Param("startOfDay") LocalDateTime startOfDay,
                                                         @Param("endOfDay") LocalDateTime endOfDay);


    List<Appointment> findAppointmentsByCustomerIdAndPurchaseIdOrderByStartTime(Integer customerId, Integer id);


    List<Appointment> findAppointmentsByPurchaseIdOrderByStartTime(Integer purchaseId);
}
