package npng.handdoc.diagnosis.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.ChatLog;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.domain.type.MessageType;
import npng.handdoc.diagnosis.domain.type.Sender;
import npng.handdoc.diagnosis.dto.request.SignLogReq;
import npng.handdoc.diagnosis.dto.response.ClovaCsrRes;
import npng.handdoc.diagnosis.dto.response.SummaryRes;
import npng.handdoc.diagnosis.exception.DiagnosisException;
import npng.handdoc.diagnosis.exception.errorcode.DiagnosisErrorCode;
import npng.handdoc.diagnosis.repository.DiagnosisRepository;
import npng.handdoc.diagnosis.util.NaverCsrClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final NaverCsrClient naverCsrClient;

    // 진료 시작
    @Transactional
    public Diagnosis startDiagnosis(){
        Diagnosis diagnosis = Diagnosis.builder()
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        return diagnosisRepository.save(diagnosis);
    }

    // 진료 종료
    @Transactional
    public void endDiagnosis(String diagnosisId){
        Diagnosis diagnosis = findDiagnosisOrThrow(diagnosisId);
        diagnosis.endNow();
        diagnosisRepository.save(diagnosis);
    }

    // 수어 등록
    @Transactional
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

    // 진료 내용 요약
    @Transactional
    public SummaryRes getSummary(String diagnosisId){
        Diagnosis diagnosis = findDiagnosisOrThrow(diagnosisId);
        return null;
    }

    // 진료 시간 계산
    private String calculateTime(Diagnosis diagnosis){
        LocalDateTime start = diagnosis.getCreatedAt();
        LocalDateTime end = diagnosis.getEndedAt();
        return toHHMMSS(Duration.between(start, end));
    }

    private static String toHHMMSS(Duration d) {
        long seconds = d.getSeconds();
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
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
