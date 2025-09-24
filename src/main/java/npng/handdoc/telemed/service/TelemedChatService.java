package npng.handdoc.telemed.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.dto.response.SummaryAIResponse;
import npng.handdoc.diagnosis.dto.response.SummaryResponse;
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
import npng.handdoc.telemed.dto.request.SignRequest;
import npng.handdoc.telemed.dto.response.SpeechCandidateResponse;
import npng.handdoc.telemed.exception.TelemedException;
import npng.handdoc.telemed.exception.errorcode.TelemedErrorCode;
import npng.handdoc.telemed.repository.TelemedChatRepository;
import npng.handdoc.telemed.repository.TelemedRepository;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.exception.errorcode.UserErrorCode;
import npng.handdoc.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TelemedChatService {

    private final TelemedRepository telemedRepository;
    private final TelemedChatRepository telemedChatRepository;
    private final UserRepository userRepository;
    private final NaverCsrClient naverCsrClient;
    private final OpenAIService openAIService;

    // 수어 등록
    @Transactional
    public void saveSign(Long userId, String roomId, SignRequest request){
        User user = findUserOrElse(userId);
        Telemed telemed = findRoomOrElse(roomId);
        validatePatientAccess(telemed, user);

        TelemedChatLog.Message messgae = TelemedChatLog.Message.builder()
                .sender(Sender.PATIENT)
                .messageType(MessageType.MTT)
                .message(request.message())
                .timestamp(LocalDateTime.now())
                .build();

        TelemedChatLog chatLog = telemedChatRepository.findByRoomId(roomId)
                .orElseGet(()-> new TelemedChatLog(null, roomId, new ArrayList<>()));
        chatLog.getMessageList().add(messgae);
        telemedChatRepository.save(chatLog);
    }

    // 음성 -> 텍스트 변환 후 저장
    @Transactional
    public String saveSpeechText(Long userId, String roomId, MultipartFile file) throws IOException {
        User user = findUserOrElse(userId);
        Telemed telemed = findRoomOrElse(roomId);
        validateDoctorAccess(telemed, user);

        ClovaCsrRes speechText = naverCsrClient.transcribe(file.getBytes());
        String text = speechText.text();

        TelemedChatLog.Message messgae = TelemedChatLog.Message.builder()
                .sender(Sender.DOCTOR)
                .messageType(MessageType.STT)
                .timestamp(LocalDateTime.now())
                .build();

        TelemedChatLog chatLog = telemedChatRepository.findByRoomId(roomId)
                .orElseGet(()-> new TelemedChatLog(null, roomId, new ArrayList<>()));
        chatLog.getMessageList().add(messgae);
        telemedChatRepository.save(chatLog);
        return text;
    }

    // 음성 -> 텍스트 변환 후 GPT 전송하여 3가지 예시 답변 반환
    @Transactional
    public SpeechCandidateResponse getSpeechText(Long userId, String roomId, MultipartFile file) throws IOException {
        User user = findUserOrElse(userId);
        Telemed telemed = findRoomOrElse(roomId);
        validatePatientAccess(telemed, user);

        ClovaCsrRes speechText = naverCsrClient.transcribe(file.getBytes());
        String text = speechText.text();

        List<String> candidates = openAIService.generateCandidates(text);

        return SpeechCandidateResponse.from(candidates);
    }

    // 진료 내용 요약
    @Transactional
    public SummaryResponse saveSummary(String roomId){
        Telemed telemed = findRoomOrElse(roomId);
        validateInactive(telemed);
        TelemedChatLog telemedChatLog = findChatLogOrElse(roomId);

        SummaryAIResponse summaryAIRes = openAIService.summarize(telemedChatLog);
        String consultationTime = calculateTime(telemed);
        Summary summary = Summary.builder()
                .consultationTime(consultationTime)
                .symptom(summaryAIRes.symptom())
                .impression(summaryAIRes.impression())
                .prescription(summaryAIRes.prescription())
                .build();
        telemed.addSummary(summary);

        telemedRepository.save(telemed);
        return SummaryResponse.of(consultationTime, summaryAIRes.symptom(), summaryAIRes.impression(), summaryAIRes.prescription());
    }

    private User findUserOrElse(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Telemed findRoomOrElse(String roomId) {
        return telemedRepository.findById(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private TelemedChatLog findChatLogOrElse(String roomId) {
        return telemedChatRepository.findByRoomId(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private void validateInactive(Telemed telemed) {
        if (telemed.getDiagnosisStatus() == DiagnosisStatus.ACTIVE) {
            throw new DiagnosisException(DiagnosisErrorCode.DIAGNOSIS_NOT_ENDED);
        }
    }

    private String calculateTime(Telemed telemed) {
        LocalDateTime start = telemed.getStartedAt();
        LocalDateTime end = telemed.getEndedAt();
        return toHHMMSS(Duration.between(start, end));
    }

    private static String toHHMMSS(Duration d) {
        long seconds = d.getSeconds();
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void validatePatientAccess(Telemed telemed, User user){
        if (telemed.getDiagnosisStatus() != DiagnosisStatus.ACTIVE){
            throw new TelemedException(TelemedErrorCode.ALREADY_ROOM_ENDED);
        }
        if (!telemed.getPatientId().equals(user.getId())){
            throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
        }
    }

    private void validateDoctorAccess(Telemed telemed, User user){
        if (telemed.getDiagnosisStatus() != DiagnosisStatus.ACTIVE){
            throw new TelemedException(TelemedErrorCode.ALREADY_ROOM_ENDED);
        }
        if(!telemed.getDoctorId().equals(user.getId())){
            throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
        }
    }
}
