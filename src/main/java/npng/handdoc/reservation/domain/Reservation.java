package npng.handdoc.reservation.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.reservation.domain.type.Option;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import npng.handdoc.reservation.domain.type.Symptom;
import npng.handdoc.reservation.exception.ReservationException;
import npng.handdoc.reservation.exception.errorcode.ReservationErrorCode;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name="reservation")
@RequiredArgsConstructor
public class Reservation extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="symptom")
    private Symptom symptom;

    @Column(name="symptom_duration")
    private Long symptomDuration;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ReservationStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "reservation_interpretation", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name="interpretation_option")
    @Enumerated(EnumType.STRING)
    private Set<Option> interpretationOption = new HashSet<>();

    @Column(name="description")
    private String description;

    @Column(name="slot_date")
    private LocalDate slotDate;

    @Column(name="start_time")
    private LocalTime startTime;

    @Column(name="end_time")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="doctor_profile_id")
    private DoctorProfile doctorProfile;

    public void changeStatus(ReservationStatus reservationStatus){
        ReservationStatus status = this.status;

        // 동일 상태 요청 허용
        if (status == reservationStatus) return;

        // 수락 또는 거절 상태 변화
        if (status == ReservationStatus.REQUESTED &&
                (reservationStatus == ReservationStatus.CONFIRMED || reservationStatus == ReservationStatus.CANCELED)){
            this.status = reservationStatus;
            return;
        }
        throw new ReservationException(ReservationErrorCode.INVALID_STATUS_TRANSITION);
    }

    @Builder
    public Reservation(User user, DoctorProfile doctor, ReservationStatus status, Symptom symptom, Long symptomDuration, String description, LocalDate slotDate, LocalTime startTime, LocalTime endTime){
        this.user = user;
        this.doctorProfile = doctor;
        this.status = status;
        this.symptom = symptom;
        this.symptomDuration = symptomDuration;
        this.description = description;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
