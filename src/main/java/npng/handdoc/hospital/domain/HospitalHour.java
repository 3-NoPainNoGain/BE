package npng.handdoc.hospital.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.hospital.domain.type.Day;

import java.time.LocalTime;

@Entity
@Getter
@Table(name = "hospital_hour")
@RequiredArgsConstructor
public class HospitalHour extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hospital_id")
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private Day day;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Builder
    public HospitalHour(Hospital hospital, Day day, LocalTime openTime, LocalTime closeTime) {
        this.hospital = hospital;
        this.day = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
