package npng.handdoc.telemed.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.dto.response.SummaryAIResponse;
import npng.handdoc.global.util.openai.service.OpenAIService;
import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import npng.handdoc.reservation.exception.ReservationException;
import npng.handdoc.reservation.repository.ReservationRepository;
import npng.handdoc.telemed.domain.Summary;
import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.TelemedChatLog;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import npng.handdoc.telemed.dto.response.*;
import npng.handdoc.telemed.exception.TelemedException;
import npng.handdoc.telemed.exception.errorcode.TelemedErrorCode;
import npng.handdoc.telemed.repository.SummaryRepository;
import npng.handdoc.telemed.repository.TelemedChatRepository;
import npng.handdoc.telemed.repository.TelemedRepository;
import npng.handdoc.user.domain.type.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static npng.handdoc.reservation.exception.errorcode.ReservationErrorCode.RESERVATION_NOT_FOUND;
import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.*;

@Service
@RequiredArgsConstructor
public class TelemedService {

    private final ReservationRepository reservationRepository;
    private final TelemedRepository telemedRepository;
    private final TelemedChatRepository telemedChatRepository;
    private final SummaryRepository summaryRepository;

    private final OpenAIService openAIService;

    private static final String WS_URL = "wss://handdoc.store/ws/signaling";
    private static final List<JoinResponse.IceServer> DEFAULT_ICE =
            List.of(new JoinResponse.IceServer("stun:stun.l.google.com:19302"));

    @Transactional
    public JoinResponse join(Long userId, Long reservationId){
        Reservation reservation = findReservationOrElse(reservationId);
        if(reservation.getStatus() != ReservationStatus.CONFIRMED){
            throw new TelemedException(RESERVATION_NOT_CONFIRMED);
        }
        Telemed telemed = findTelemedOrElse(reservationId);
        Role role = assertParticipant(reservation, telemed, userId);

        // 방이 없으면 생성
        if(telemed == null){
            telemed = createFromReservation(reservation);
            telemedRepository.save(telemed);
        }

        if (role == Role.ROLE_PATIENT && !telemed.isPatientJoined()) telemed.markPatientJoined();
        if (role == Role.ROLE_DOCTOR  && !telemed.isDoctorJoined())  telemed.markDoctorJoined();

        // 둘 다 입장하면 ACTIVE 전환
        telemed.activateIfBothJoined();
        return JoinResponse.from(telemed, role, WS_URL, DEFAULT_ICE, reservation);
    }

    @Transactional
    public EndResponse end(Long userId, String roomId){
        Telemed telemed = findRoomOrElse(roomId);
        assertParticipant(telemed, userId);

        if(telemed.getDiagnosisStatus() == DiagnosisStatus.ENDED){
            throw new TelemedException(TelemedErrorCode.ALREADY_ROOM_ENDED);
        }

        telemed.markEnded();

        // 진료 요약 생성
        createSummary(telemed);
        return EndResponse.from(telemed);
    }

    // 진료 내역 조회
    @Transactional(readOnly = true)
    public HistoryListResponse getHistory(Pageable pageable, Long userId){
        Page<Telemed> telemedPage = telemedRepository.findByPatientIdAndDiagnosisStatusOrderByStartedAtDesc(
                        userId, DiagnosisStatus.ENDED, pageable);
        Page<HistoryItemResponse> historyItemResponsePage = telemedPage.map(t ->
                HistoryItemResponse.from(t.getReservation(), t));

        return HistoryListResponse.from(historyItemResponsePage);
    }

    // 진료 내역 상세 조회
    @Transactional(readOnly = true)
    public HistoryDetailResponse getHistoryDetail(Long userId, String roomId){
        Telemed telemed = findRoomOrElse(roomId);
        if (!telemed.getPatientId().equals(userId)) {
            throw new TelemedException(NOT_PARTICIPANT);
        }
        TelemedChatLog chatLog = findChatLogOrElse(roomId);
        Summary summary = findSummaryOrElse(roomId);
        return HistoryDetailResponse.from(chatLog, summary);
    }

    // 요약 생성
    private void createSummary(Telemed telemed) {
        if (telemed.getSummary() != null) return;
        if (summaryRepository.findByTelemed_Id(telemed.getId()).isPresent()) return;

        TelemedChatLog telemedChatLog = findChatLogOrElse(telemed.getId());
        SummaryAIResponse summaryAIRes = openAIService.summarize(telemedChatLog);
        String consultationTime = calculateTime(telemed);
        Summary summary = Summary.builder()
                .consultationTime(consultationTime)
                .symptom(summaryAIRes.symptom())
                .impression(summaryAIRes.impression())
                .prescription(summaryAIRes.prescription())
                .build();
        telemed.addSummary(summary);
    }

    // 시간 계산
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

    private Reservation findReservationOrElse(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(()-> new ReservationException(RESERVATION_NOT_FOUND));
    }

    private TelemedChatLog findChatLogOrElse(String roomId) {
        return telemedChatRepository.findByRoomId(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private Telemed findTelemedOrElse(Long reservationId) {
        return telemedRepository.findByReservationId(reservationId).orElse(null);
    }

    private Telemed findRoomOrElse(String roomId) {
        return telemedRepository.findById(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private Summary findSummaryOrElse(String roomId){
        return summaryRepository.findByTelemed_Id(roomId).orElseThrow(()-> new TelemedException(SUMMARY_NOT_FOUND));
    }

    private Telemed createFromReservation(Reservation reservation) {
        return Telemed.builder()
                .reservation(reservation)
                .doctorId(reservation.getDoctorProfile().getUser().getId())
                .patientId(reservation.getUser().getId())
                .build();
    }

    // 참가자 검증
    private Role assertParticipant(Reservation reservation, Telemed telemed, Long userId) {
        if (telemed != null){
            if (userId.equals(telemed.getPatientId())) return Role.ROLE_PATIENT;
            if (userId.equals(telemed.getDoctorId())) return Role.ROLE_DOCTOR;
        }

        Long patientId = reservation.getUser().getId();
        Long doctorId = reservation.getDoctorProfile().getUser().getId();
        if(userId.equals(patientId)) return Role.ROLE_PATIENT;
        if(userId.equals(doctorId)) return Role.ROLE_DOCTOR;

        throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
    }

    // 참가자 검증
    private void assertParticipant(Telemed telemed, Long userId) {
        if (userId.equals(telemed.getPatientId())) return;
        if (userId.equals(telemed.getDoctorId())) return;
        throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
    }
}
