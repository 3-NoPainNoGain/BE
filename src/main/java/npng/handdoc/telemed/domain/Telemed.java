package npng.handdoc.telemed.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="telemed")
@RequiredArgsConstructor
public class Telemed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // roomId

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id")
    private Reservation reservation;

    @OneToOne(mappedBy = "telemed", cascade = CascadeType.ALL, orphanRemoval = true)
    private Summary summary;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private DiagnosisStatus diagnosisStatus; // WAITING, ACTIVE, ENDED

    @Column(name="doctor_id")
    private Long doctorId;

    @Column(name="patient_id")
    private Long patientId;

    @Column(name="patient_joined")
    private boolean patientJoined = false;

    @Column(name="doctor_joined")
    private boolean doctorJoined = false;

    @Column(name="started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Builder
    public Telemed(Reservation reservation, Long doctorId, Long patientId) {
        this.reservation = reservation;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.diagnosisStatus = DiagnosisStatus.WAITING;
        this.patientJoined = false;
        this.doctorJoined = false;
    }

    public void markPatientJoined() { this.patientJoined = true; }
    public void markDoctorJoined() { this.doctorJoined = true; }

    public void activateIfBothJoined() {
        if (patientJoined && doctorJoined && diagnosisStatus != DiagnosisStatus.ACTIVE) {
            this.diagnosisStatus = DiagnosisStatus.ACTIVE;
            this.startedAt = LocalDateTime.now();
        }
    }

    public void markEnded(){
        this.diagnosisStatus = DiagnosisStatus.ENDED;
        this.endedAt = LocalDateTime.now();
    }
}
