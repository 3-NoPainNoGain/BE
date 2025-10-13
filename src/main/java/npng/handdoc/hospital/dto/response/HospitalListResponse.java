package npng.handdoc.hospital.dto.response;

import npng.handdoc.hospital.domain.Hospital;

import java.util.List;
import java.util.stream.Collectors;

public record HospitalListResponse(
        List<HospitalItemResponse> hospitalItemResponseList,
        int count
) {
    public static HospitalListResponse from(List<Hospital> hospitalList){
        List<HospitalItemResponse> hospitalItemResponseList = hospitalList.stream()
                .map(HospitalItemResponse::from)
                .collect(Collectors.toList());

        return new HospitalListResponse(hospitalItemResponseList, hospitalList.size());
    }
}
