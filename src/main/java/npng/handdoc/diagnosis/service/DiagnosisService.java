package npng.handdoc.diagnosis.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.excpetion.DiagnosisException;
import npng.handdoc.diagnosis.excpetion.errorcode.DiagnosisErrorCode;
import npng.handdoc.diagnosis.repository.DiagnosisRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    // 진료 시작
    public Diagnosis startDiagnosis(){
        Diagnosis diagnosis = Diagnosis.builder()
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        return diagnosisRepository.save(diagnosis);
    }

    // 진료 종료
    public void endDiagnosis(String diagnosisId){
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId).
                orElseThrow(()-> new DiagnosisException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));
        diagnosis.endNow();
        diagnosisRepository.save(diagnosis);
    }
}
