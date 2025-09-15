package npng.handdoc.telemed.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;

@Getter
@Entity
@Table(name="summary")
@RequiredArgsConstructor
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="consultation_time")
    private String consultationTime;

    @Column(name="symptom")
    private String symptom;

    @Column(name="impression")
    private String impression;

    @Column(name="prescription")
    private String prescription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="telemed_id")
    private Telemed telemed;

    public void setTelemed(Telemed telemed) {this.telemed = telemed;}

    @Builder
    public Summary(String consultationTime, String symptom, String impression, String prescription) {
        this.consultationTime = consultationTime;
        this.symptom = symptom;
        this.impression = impression;
        this.prescription = prescription;
    }
}
