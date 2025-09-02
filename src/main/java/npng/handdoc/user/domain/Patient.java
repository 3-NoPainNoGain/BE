package npng.handdoc.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Table(name="patient")
@RequiredArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = true)
    private String name;

    @Column(name="resident_id")
    private String residentId;

    @OneToOne(mappedBy = "patient")
    private User user;
}
