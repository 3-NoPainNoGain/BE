package npng.handdoc.telemed.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.TelemedChatLog;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import npng.handdoc.telemed.domain.type.MessageType;
import npng.handdoc.telemed.domain.type.Sender;
import npng.handdoc.telemed.dto.request.SignRequest;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TelemedChatService {

    private final TelemedRepository telemedRepository;
    private final TelemedChatRepository telemedChatRepository;
    private final UserRepository userRepository;

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

    private User findUserOrElse(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Telemed findRoomOrElse(String roomId) {
        return telemedRepository.findById(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private void validatePatientAccess(Telemed telemed, User user){
        if (telemed.getDiagnosisStatus() != DiagnosisStatus.ACTIVE){
            throw new TelemedException(TelemedErrorCode.ALREADY_ROOM_ENDED);
        }
        if (!telemed.getPatientId().equals(user.getId())){
            throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
        }
    }
}
