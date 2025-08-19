package npng.handdoc.diagnosis.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.ChatLog;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.domain.type.MessageType;
import npng.handdoc.diagnosis.domain.type.Sender;
import npng.handdoc.diagnosis.dto.request.SignLogReq;
import npng.handdoc.diagnosis.dto.response.ClovaCsrRes;
import npng.handdoc.diagnosis.exception.DiagnosisException;
import npng.handdoc.diagnosis.exception.errorcode.DiagnosisErrorCode;
import npng.handdoc.diagnosis.repository.DiagnosisRepository;
import npng.handdoc.speech.client.NaverCsrClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final NaverCsrClient naverCsrClient;

    // 진료 시작
    public Diagnosis startDiagnosis(){
        Diagnosis diagnosis = Diagnosis.builder()
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        return diagnosisRepository.save(diagnosis);
    }

    // 진료 종료
    public void endDiagnosis(String diagnosisId){
        Diagnosis diagnosis = findDiagnosisOrThrow(diagnosisId);
        diagnosis.endNow();
        diagnosisRepository.save(diagnosis);
    }

    // 수어 등록
    public void saveSignText(String diagnosisId, SignLogReq request){
        Diagnosis diagnosis = findDiagnosisOrThrow(diagnosisId);
        validateActive(diagnosis);
        ChatLog chatLog = ChatLog.builder()
                .sender(Sender.PATIENT)
                .messageType(MessageType.MTT)
                .message(request.message())
                .build();

        diagnosis.addChatLog(chatLog);
        diagnosisRepository.save(diagnosis);
    }

    // 음성 -> 텍스트 변환 후 저장
    @Transactional
    public String saveSpeechText(String diagnosisId, MultipartFile file) throws IOException {
        Diagnosis diagnosis = findDiagnosisOrThrow(diagnosisId);
        validateActive(diagnosis);
        ClovaCsrRes speechText = naverCsrClient.transcribe(file.getBytes());
        String text = speechText.text();
        ChatLog chatLog = ChatLog.builder()
                .sender(Sender.DOCTOR)
                .messageType(MessageType.STT)
                .message(text)
                .build();
        diagnosis.addChatLog(chatLog);
        diagnosisRepository.save(diagnosis);
        return text;
    }

    private Diagnosis findDiagnosisOrThrow(String diagnosisId){
        return diagnosisRepository.findById(diagnosisId)
                .orElseThrow(()-> new DiagnosisException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));
    }

    private void validateActive(Diagnosis diagnosis) {
        if (!diagnosis.isActive()) {
            throw new DiagnosisException(DiagnosisErrorCode.DIAGNOSIS_ALREADY_ENDED);
        }
    }
}
