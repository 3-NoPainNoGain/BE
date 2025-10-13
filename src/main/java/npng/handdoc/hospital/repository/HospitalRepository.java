package npng.handdoc.hospital.repository;

import npng.handdoc.hospital.domain.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
