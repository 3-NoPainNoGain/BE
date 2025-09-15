package npng.handdoc.telemed.domain;

import jakarta.persistence.*;
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

    @Column(name="symptom")
    private String symptom;

    @Column(name="impression")
    private String impression;

    @Column(name="prescription")
    private String prescription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="telemed_id")
    private Telemed telemed;
}
