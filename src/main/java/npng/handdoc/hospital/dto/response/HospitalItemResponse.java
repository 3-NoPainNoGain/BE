package npng.handdoc.hospital.dto.response;

import npng.handdoc.hospital.domain.Hospital;

import java.util.List;
import java.util.stream.Collectors;

public record HospitalItemResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        List<HospitalHourResponse> operatingHours
) {
    public static HospitalItemResponse from(Hospital hospital){
        return new HospitalItemResponse(
                hospital.getId(),
                hospital.getName(),
                hospital.getAddress(),
                hospital.getLatitude(),
                hospital.getLongitude(),
                hospital.getHospitalHourList().stream()
                        .map(HospitalHourResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
