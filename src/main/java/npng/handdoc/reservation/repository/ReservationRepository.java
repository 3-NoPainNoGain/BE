package npng.handdoc.reservation.repository;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findAllByDoctorProfile_Id(Long doctorProfileId, Pageable pageable);
    Page<Reservation> findAllByUser_Id(Long userId, Pageable pageable);
    long countByDoctorProfile_IdAndStatus(Long doctorProfileId, ReservationStatus status);

    // 의사 유저ID 기준 조회/검증
    Page<Reservation> findAllByDoctorProfile_User_Id(Long doctorUserId, Pageable pageable);
    Optional<Reservation> findByIdAndDoctorProfile_User_Id(Long reservationId, Long doctorUserId);


    boolean existsByDoctorProfile_IdAndSlotDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorProfileId,
            java.time.LocalDate slotDate,
            java.time.LocalTime endTimeExclusive,
            java.time.LocalTime startTimeExclusive
    );

    @Query(value = """
        select * 
          from reservation 
         where user_id = :userId 
         order by id desc 
         limit 1
        """, nativeQuery = true)
    Optional<Reservation> findByUserId(@Param("userId") Long userId);
}
