package npng.handdoc.user.dto.response;

import lombok.Builder;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.type.DoctorStatus;
import npng.handdoc.user.domain.type.Speciality;

@Builder
public record DoctorDetailResponse(
        Long id,
        String name,
        Speciality speciality,
        String hospitalName,
        String introduction,
        DoctorStatus status
) {
    public static DoctorDetailResponse from(DoctorProfile doctor) {
        return DoctorDetailResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .speciality(doctor.getSpeciality())
                .hospitalName(doctor.getHospitalName())
                .introduction(doctor.getIntroduction())
                .status(doctor.getStatus())
                .build();
    }
}