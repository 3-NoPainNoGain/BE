package npng.handdoc.reservation.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import npng.handdoc.reservation.dto.request.ReservationAcceptOrDenyRequest;
import npng.handdoc.reservation.dto.request.ReservationCreateRequest;
import npng.handdoc.reservation.dto.response.ReservationListResponse;
import npng.handdoc.reservation.dto.response.ReservationResponse;
import npng.handdoc.reservation.exception.ReservationException;
import npng.handdoc.reservation.exception.errorcode.ReservationErrorCode;
import npng.handdoc.reservation.repository.ReservationRepository;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.exception.errorcode.UserErrorCode;
import npng.handdoc.user.repository.DoctorProfileRepository;
import npng.handdoc.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    // 환자 예약 생성
    @Transactional
    public ReservationResponse create(Long userId, ReservationCreateRequest request) {

        if (!request.startTime().isBefore(request.endTime())) {
            throw new ReservationException(ReservationErrorCode.INVALID_TIME_RANGE);
        }

        User user = getUserOrThrow(userId);
        DoctorProfile doctor = getDoctorOrThrow(request.doctorProfileId());

        Reservation reservation = Reservation.builder()
                .user(user)
                .doctor(doctor)
                .status(ReservationStatus.REQUESTED)
                .symptom(request.symptom())
                .symptomDuration(request.symptomDuration())
                .description(request.description())
                .slotDate(request.slotDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    // 환자 예약 취소
    @Transactional
    public void cancel(Long reservationId, Long userId) {
        Reservation reservation = getReservationOrThrowByPatient(userId, reservationId);
        if (reservation.getStatus() == ReservationStatus.CANCELED) return;
        reservation.changeStatus(ReservationStatus.CANCELED);
    }

    @Transactional(readOnly = true)
    public ReservationListResponse getReservationList(Long doctorProfileId, Pageable pageable) {
        Page<Reservation> reservationPage = reservationRepository.findByDoctorProfile_Id(doctorProfileId, pageable);
        Page<ReservationResponse> reservationResponsePage = reservationPage.map(r -> ReservationResponse.from(r));
        return ReservationListResponse.from(reservationResponsePage);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long userId, Long reservationId) {
        Reservation reservation = getReservationOrThrowByPatient(userId, reservationId);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void acceptOrDeny(Long reservationId, Long doctorUserId, ReservationAcceptOrDenyRequest request) {
        Reservation reservation = getReservationOrThrowByDoctor(doctorUserId, reservationId);
        ReservationStatus status = request.accept() ? ReservationStatus.CONFIRMED : ReservationStatus.CANCELED;
        reservation.changeStatus(status);
    }

    private Reservation getReservationOrThrowByDoctor(Long reservationId, Long doctorProfileId) {
        return reservationRepository.findByIdAndDoctorProfile_Id(reservationId, doctorProfileId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    private Reservation getReservationOrThrowByPatient(Long userId, Long reservationId){
        return reservationRepository.findByIdAndUser_Id(reservationId, userId).orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private DoctorProfile getDoctorOrThrow(Long doctorProfileId) {
        return doctorProfileRepository.findById(doctorProfileId).orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
