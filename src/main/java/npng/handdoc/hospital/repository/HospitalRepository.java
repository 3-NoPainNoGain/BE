package npng.handdoc.hospital.repository;

import npng.handdoc.hospital.domain.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    @Query(
            value = "SELECT * FROM hospital h " +
                    "WHERE ST_Distance_Sphere(POINT(h.longitude, h.latitude), POINT(:lon, :lat)) <= :radiusMeters " +
                    "ORDER BY ST_Distance_Sphere(POINT(h.longitude, h.latitude), POINT(:lon, :lat)) " +
                    "LIMIT 20",
            nativeQuery = true
    )
    List<Hospital> findNearbyHospitals(
            @Param("lon") Double longitude,
            @Param("lat") Double latitude,
            @Param("radiusMeters") Double radiusMeters
    );
}
