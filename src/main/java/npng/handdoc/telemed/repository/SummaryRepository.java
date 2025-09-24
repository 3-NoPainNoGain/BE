package npng.handdoc.telemed.repository;

import npng.handdoc.telemed.domain.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findByTelemed_Id(String telemedId);
}
