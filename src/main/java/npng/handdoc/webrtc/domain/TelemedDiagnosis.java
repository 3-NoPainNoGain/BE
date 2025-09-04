package npng.handdoc.webrtc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.webrtc.domain.type.DiagnosisStatus;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="diagnosis_webrtc")
@RequiredArgsConstructor
public class Diagnosis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id")
    private Reservation reservation;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private DiagnosisStatus diagnosisStatus;

    @Column(name="doctor_id")
    private Long doctorId;

    @Column(name="patient_id")
    private Long patientId;

    @Column(name="started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
