package npng.handdoc.user.dto.response;

import lombok.Builder;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.domain.type.DoctorStatus;
import npng.handdoc.user.domain.type.Speciality;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DoctorDetailResponse(
        Long id,
        String name,
        String speciality,
        String hospitalName,
        String introduction,
        String status,
        List<DoctorTagResponse> doctorTagList
) {
    public static DoctorDetailResponse from(DoctorProfile doctor) {
        return DoctorDetailResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .speciality(doctor.getSpeciality().getLabel())
                .hospitalName(doctor.getHospitalName())
                .introduction(doctor.getIntroduction())
                .status(doctor.getStatus().getLabel())
                .doctorTagList(doctor.getDoctorTagList()
                        .stream()
                        .map(DoctorTagResponse::from)
                        .toList())
                .build();
    }
}