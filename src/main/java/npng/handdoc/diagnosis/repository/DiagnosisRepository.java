package npng.handdoc.diagnosis.repository;

import npng.handdoc.diagnosis.domain.Diagnosis;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiagnosisRepository extends MongoRepository<Diagnosis, String> {
}