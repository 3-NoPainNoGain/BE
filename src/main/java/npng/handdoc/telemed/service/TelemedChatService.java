package npng.handdoc.telemed.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.telemed.dto.response.SummaryResponse;
import npng.handdoc.diagnosis.exception.DiagnosisException;
import npng.handdoc.diagnosis.exception.errorcode.DiagnosisErrorCode;
import npng.handdoc.global.util.naver.NaverCsrClient;
import npng.handdoc.global.util.naver.dto.ClovaCsrRes;
import npng.handdoc.global.util.openai.service.OpenAIService;
import npng.handdoc.telemed.domain.Summary;
import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.TelemedChatLog;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import npng.handdoc.telemed.domain.type.MessageType;
import npng.handdoc.telemed.domain.type.Sender;
import npng.handdoc.telemed.dto.request.SendSpeechRequest;
import npng.handdoc.telemed.dto.request.SignRequest;
import npng.handdoc.telemed.dto.response.SpeechCandidateResponse;
import npng.handdoc.telemed.exception.TelemedException;
import npng.handdoc.telemed.exception.errorcode.TelemedErrorCode;
import npng.handdoc.telemed.repository.SummaryRepository;
import npng.handdoc.telemed.repository.TelemedChatRepository;
import npng.handdoc.telemed.repository.TelemedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.ROOM_NOT_FOUND;
import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.SUMMARY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TelemedChatService {

    private final TelemedRepository telemedRepository;
    private final TelemedChatRepository telemedChatRepository;
    private final NaverCsrClient naverCsrClient;
    private final OpenAIService openAIService;
    private final SummaryRepository summaryRepository;

    // (환자) 수어 등록
    @Transactional
    public void saveSign(Long userId, String roomId, SignRequest request){
        Telemed telemed = findRoomOrElse(roomId);
        validateActive(telemed);
        assertParticipant(telemed, userId);

        appendMessage(roomId, Sender.PATIENT, MessageType.MTT, request.message());
    }

    // (의사) 음성 -> 텍스트 변환 후 저장
    @Transactional
    public String saveDoctorSpeechText(Long userId, String roomId, MultipartFile file) throws IOException {
        Telemed telemed = findRoomOrElse(roomId);
        validateActive(telemed);
        assertParticipant(telemed, userId);

        ClovaCsrRes speechText = naverCsrClient.transcribe(file.getBytes());
        String text = speechText.text();

        appendMessage(roomId, Sender.DOCTOR, MessageType.STT, text);
        return text;
    }

    // (환자) 음성 -> 텍스트 변환 후 저장
    @Transactional
    public String savePatientNormalSpeechText(Long userId, String roomId, MultipartFile file) throws IOException {
        Telemed telemed = findRoomOrElse(roomId);
        validateActive(telemed);
        assertParticipant(telemed, userId);

        ClovaCsrRes normalText = naverCsrClient.transcribe(file.getBytes());
        String text = normalText.text();

        appendMessage(roomId, Sender.PATIENT, MessageType.STT, text);
        return text;
    }

    // (환자) 음성 -> 텍스트 변환하여 텍스트 반환
    @Transactional(readOnly = true)
    public String transcribe(Long userId, String roomId, MultipartFile file) throws IOException {
        Telemed telemed = findRoomOrElse(roomId);
        validateActive(telemed);
        assertParticipant(telemed, userId);

        ClovaCsrRes speechText = naverCsrClient.transcribe(file.getBytes());
        return speechText.text();
    }


    // (환자) 변환된 텍스트를 GPT 전송하여 3가지 예시 답변 반환
    @Transactional(readOnly = true)
    public SpeechCandidateResponse getSpeechText(Long userId, String roomId, SendSpeechRequest request){
        Telemed telemed = findRoomOrElse(roomId);
        validateActive(telemed);
        assertParticipant(telemed, userId);

        List<String> candidates = openAIService.generateCandidates(request.recordedText());
        return SpeechCandidateResponse.from(candidates);
    }

    // (환자) 3가지 후보 중 하나를 선택하여 전송
    @Transactional
    public void savePatientSpeechText(Long userId, String roomId, String selectedText){
        Telemed telemed = findRoomOrElse(roomId);
        validateActive(telemed);
        assertParticipant(telemed, userId);

        appendMessage(roomId, Sender.PATIENT, MessageType.STT, selectedText);
    }

    // (환자, 의사) 진료 내용 요약 조회
    @Transactional
    public SummaryResponse getSummary(Long userId, String roomId){
        Telemed telemed = findRoomOrElse(roomId);
        validateInactive(telemed);
        assertParticipant(telemed, userId);

        Summary summary = findSummaryOrElse(roomId);
        return SummaryResponse.from(summary);
    }

    // 메시지 저장 공통화
    private void appendMessage(String roomId, Sender sender, MessageType type, String text){
        TelemedChatLog chatLog = telemedChatRepository.findByRoomId(roomId)
                .orElseGet(() -> new TelemedChatLog(null, roomId, new ArrayList<>()));

        chatLog.getMessageList().add(
                TelemedChatLog.Message.builder()
                        .sender(sender)
                        .messageType(type)
                        .message(text)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        telemedChatRepository.save(chatLog);
    }

    private Telemed findRoomOrElse(String roomId) {
        return telemedRepository.findById(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private Summary findSummaryOrElse(String roomId){
        return summaryRepository.findByTelemed_Id(roomId).orElseThrow(()-> new TelemedException(SUMMARY_NOT_FOUND));
    }

    // 진행 중만 허용
    private void validateActive(Telemed telemed) {
        if (telemed.getDiagnosisStatus() != DiagnosisStatus.ACTIVE) {
            throw new TelemedException(TelemedErrorCode.ALREADY_ROOM_ENDED);
        }
    }

    // 종료 후만 허용
    private void validateInactive(Telemed telemed) {
        if (telemed.getDiagnosisStatus() == DiagnosisStatus.ACTIVE) {
            throw new DiagnosisException(DiagnosisErrorCode.DIAGNOSIS_NOT_ENDED);
        }
    }

    // 참가자 검증
    private void assertParticipant(Telemed telemed, Long userId) {
        if (userId.equals(telemed.getPatientId())) return;
        if (userId.equals(telemed.getDoctorId())) return;
        throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
    }
}
