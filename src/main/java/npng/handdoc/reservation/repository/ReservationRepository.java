package npng.handdoc.reservation.repository;

import npng.handdoc.reservation.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndDoctorProfile_Id(Long reservationId, Long doctorProfileId);

    @EntityGraph(attributePaths = {"user", "doctorProfile"})
    Page<Reservation> findByDoctorProfile_Id(Long doctorProfileId, Pageable pageable);

    Optional<Reservation> findByIdAndUser_Id(Long reservationId, Long userId);

    Optional<Reservation> findByUserId(Long userId);
}
