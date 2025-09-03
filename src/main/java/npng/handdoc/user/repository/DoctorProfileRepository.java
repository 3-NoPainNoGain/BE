package npng.handdoc.user.repository;

import npng.handdoc.user.domain.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorProfile, Long> {

}
