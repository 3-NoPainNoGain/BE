package npng.handdoc.telemed.repository;

import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelemedRepository extends JpaRepository<Telemed, String> {
    Optional<Telemed> findByReservationId(Long reservationId);
    Optional<Telemed> findById(String roomId);
    Optional<Telemed> findByPatientId(Long userId);

    @EntityGraph(attributePaths = {
            "reservation",
            "reservation.doctorProfile",
            "reservation.doctorProfile.user",
            "summary"
    })
    Page<Telemed> findByPatientIdAndDiagnosisStatusOrderByStartedAtDesc(
            Long patientId,
            DiagnosisStatus status,
            Pageable pageable
    );
}
