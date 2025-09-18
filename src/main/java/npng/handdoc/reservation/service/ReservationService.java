package npng.handdoc.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import npng.handdoc.reservation.domain.type.Symptom;
import npng.handdoc.reservation.dto.request.ReservationAcceptRequest;
import npng.handdoc.reservation.dto.request.ReservationCreateRequest;
import npng.handdoc.reservation.dto.response.ReservationListResponse;
import npng.handdoc.reservation.dto.response.ReservationResponse;
import npng.handdoc.reservation.exception.ReservationException;
import npng.handdoc.reservation.exception.errorcode.ReservationErrorCode;
import npng.handdoc.reservation.repository.ReservationRepository;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @PersistenceContext
    private EntityManager em;

    private ReservationResponse toResponse(Reservation e) {
        return new ReservationResponse(
                e.getId(),
                e.getUser() != null ? e.getUser().getId() : null,
                e.getDoctorProfile() != null ? e.getDoctorProfile().getId() : null,
                e.getStatus() != null ? e.getStatus().name() : null,
                e.getSymptom() != null ? e.getSymptom().name() : null,
                e.getSymptomDuration(),
                e.getDescription(),
                e.getSlotDate(),
                e.getStartTime(),
                e.getEndTime(),
                null,
                null
        );
    }

    private ReservationListResponse toListResponse(Page<Reservation> page) {
        var items = page.getContent().stream().map(this::toResponse).toList();
        var meta = new ReservationListResponse.PageMeta(
                page.getSize(),
                page.getNumber(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return new ReservationListResponse(items, meta);
    }


    // 환자 예약 생성
    @Transactional
    public ReservationResponse create(Long patientUserId, Long doctorProfileId, ReservationCreateRequest req) {

        if (!req.startTime().isBefore(req.endTime())) {
            throw new ReservationException(ReservationErrorCode.INVALID_TIME_RANGE);
        }

        User patient = em.getReference(User.class, patientUserId);
        DoctorProfile doctor = em.getReference(DoctorProfile.class, doctorProfileId);

        var entity = new Reservation();
        entity.setUser(patient);
        entity.setDoctorProfile(doctor);
        entity.setStatus(ReservationStatus.REQUESTED);

        if (req.symptom() != null && !req.symptom().isBlank()) {
            try {
                entity.setSymptom(Symptom.valueOf(req.symptom()));
            } catch (IllegalArgumentException ex) {
                throw new ReservationException(ReservationErrorCode.ACCESS_DENIED);
            }
        }
        entity.setSymptomDuration(req.symptomDuration());
        entity.setDescription(req.description());
        entity.setSlotDate(req.slotDate());
        entity.setStartTime(req.startTime());
        entity.setEndTime(req.endTime());

        var saved = reservationRepository.save(entity);
        return toResponse(saved);
    }

    // 환자 예약 취소
    @Transactional
    public void cancel(Long reservationId, Long requesterUserId) {
        var e = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!e.getUser().getId().equals(requesterUserId)) {
            throw new ReservationException(ReservationErrorCode.ACCESS_DENIED);
        }
        if (e.getStatus() == ReservationStatus.CANCELED || e.getStatus() == ReservationStatus.COMPLETED) {
            return;
        }
        e.setStatus(ReservationStatus.CANCELED);
    }

    // 의사 예약 전체 조회 — 의사 "유저ID"로 바로 검색
    @Transactional(readOnly = true)
    public ReservationListResponse listForDoctor(Long doctorUserId, int size, int page) {
        var pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "slotDate").and(Sort.by("startTime")));
        var p = reservationRepository.findAllByDoctorProfile_User_Id(doctorUserId, pageable);
        return toListResponse(p);
    }

    // (공통) 단건 상세
    @Transactional(readOnly = true)
    public ReservationResponse getOne(Long reservationId) {
        var e = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        return toResponse(e);
    }

    // 의사 수락/거부 — 의사 유저ID로 소유권 검증
    @Transactional
    public ReservationResponse acceptOrDeny(Long reservationId, Long doctorUserId, ReservationAcceptRequest req) {
        var e = reservationRepository.findByIdAndDoctorProfile_User_Id(reservationId, doctorUserId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.ACCESS_DENIED));

        e.setStatus(Boolean.TRUE.equals(req.accept())
                ? ReservationStatus.CONFIRMED
                : ReservationStatus.CANCELED);

        return toResponse(e);
    }
}
