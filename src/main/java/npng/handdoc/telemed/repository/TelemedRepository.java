package npng.handdoc.telemed.repository;

import npng.handdoc.telemed.domain.Telemed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelemedRepository extends JpaRepository<Telemed, String> {
    Optional<Telemed> findByReservationId(Long reservationId);
    Optional<Telemed> findById(String roomId);
}
