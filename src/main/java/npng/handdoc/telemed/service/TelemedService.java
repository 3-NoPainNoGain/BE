package npng.handdoc.telemed.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import npng.handdoc.reservation.exception.ReservationException;
import npng.handdoc.reservation.repository.ReservationRepository;
import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import npng.handdoc.telemed.dto.response.EndResponse;
import npng.handdoc.telemed.dto.response.JoinResponse;
import npng.handdoc.telemed.exception.TelemedException;
import npng.handdoc.telemed.exception.errorcode.TelemedErrorCode;
import npng.handdoc.telemed.repository.TelemedRepository;
import npng.handdoc.user.domain.type.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static npng.handdoc.reservation.exception.errorcode.ReservationErrorCode.RESERVATION_NOT_FOUND;
import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.RESERVATION_NOT_CONFIRMED;
import static npng.handdoc.telemed.exception.errorcode.TelemedErrorCode.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TelemedService {

    private final ReservationRepository reservationRepository;
    private final TelemedRepository telemedRepository;

    private static final String WS_URL = "wss://handdoc.store/ws/signaling";
    private static final List<JoinResponse.IceServer> DEFAULT_ICE =
            List.of(new JoinResponse.IceServer("stun:stun.l.google.com:19302"));

    public JoinResponse join(Long userId, Long reservationId){
        Reservation reservation = findReservationOrElse(reservationId);
        if(reservation.getStatus() != ReservationStatus.CONFIRMED){
            throw new TelemedException(RESERVATION_NOT_CONFIRMED);
        }
        Telemed telemed = findTelemedOrELse(reservationId);
        Role role = resolveRole(reservation, telemed, userId);

        // 방이 없으면 생성
        if(telemed == null){
            telemed = createFromReservation(reservation);
            telemedRepository.save(telemed);
        }

        if (role == Role.ROLE_PATIENT && !telemed.isPatientJoined()) telemed.markPatientJoined();
        if (role == Role.ROLE_DOCTOR  && !telemed.isDoctorJoined())  telemed.markDoctorJoined();

        // 둘 다 입장하면 ACTIVE 전환
        telemed.activateIfBothJoined();
        telemedRepository.save(telemed);
        return JoinResponse.from(telemed, role, WS_URL, DEFAULT_ICE);
    }

    @Transactional
    public EndResponse end(Long userId, String roomId){
        Telemed telemed = findRoomOrElse(roomId);
        Role role = resolveRole(telemed, userId);

        if(telemed.getDiagnosisStatus() == DiagnosisStatus.ENDED){
            throw new TelemedException(TelemedErrorCode.ALREADY_ROOM_ENDED);
        }

        telemed.markEnded();
        return EndResponse.from(telemed);
    }

    private Reservation findReservationOrElse(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(()-> new ReservationException(RESERVATION_NOT_FOUND));
    }

    private Telemed findTelemedOrELse(Long reservationId) {
        return telemedRepository.findByReservationId(reservationId).orElse(null);
    }

    private Telemed findRoomOrElse(String roomId) {
        return telemedRepository.findById(roomId).orElseThrow(()-> new TelemedException(ROOM_NOT_FOUND));
    }

    private Telemed createFromReservation(Reservation reservation) {
        return Telemed.builder()
                .reservation(reservation)
                .doctorId(reservation.getDoctorProfile().getUser().getId())
                .patientId(reservation.getUser().getId())
                .build();
    }

    private Role resolveRole(Reservation reservation, Telemed telemed, Long userId) {
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

    private Role resolveRole(Telemed telemed, Long userId) {
        if (userId.equals(telemed.getPatientId())) return Role.ROLE_PATIENT;
        if (userId.equals(telemed.getDoctorId())) return Role.ROLE_DOCTOR;
        throw new TelemedException(TelemedErrorCode.NOT_PARTICIPANT);
    }
}
