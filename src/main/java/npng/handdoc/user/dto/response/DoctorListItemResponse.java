package npng.handdoc.user.dto.response;

import lombok.Builder;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.type.DoctorStatus;

@Builder
public record DoctorListItemResponse(
        Long id,
        String name,
        String hospitalName,
        String status
) {
    public static DoctorListItemResponse from(DoctorProfile doctor) {
        return DoctorListItemResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .hospitalName(doctor.getHospitalName())
                .status(doctor.getStatus().getLabel())
                .build();
    }
}
