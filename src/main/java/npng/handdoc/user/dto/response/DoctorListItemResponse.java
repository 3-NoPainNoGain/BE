package npng.handdoc.user.dto.response;

import lombok.Builder;
import npng.handdoc.user.domain.Doctor;
import npng.handdoc.user.domain.type.DoctorStatus;

@Builder
public record DoctorListItemResponse(
        Long id,
        String name,
        String hospitalName,
        DoctorStatus status
) {
    public static DoctorListItemResponse from(Doctor doctor) {
        return DoctorListItemResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .hospitalName(doctor.getHospitalName())
                .status(doctor.getStatus())
                .build();
    }
}
