package npng.handdoc.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.reservation.domain.type.ReservationStatus;
import npng.handdoc.reservation.domain.type.Symptom;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.User;
import npng.handdoc.webrtc.domain.TelemedDiagnosis;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name="reservation")
@RequiredArgsConstructor
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="symptom")
    @Enumerated(EnumType.STRING)
    private Symptom symptom;

    @Column(name="symptom_duration")
    private Long symptomDuration;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

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
}
