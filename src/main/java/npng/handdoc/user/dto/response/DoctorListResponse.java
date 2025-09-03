package npng.handdoc.user.dto.response;

import npng.handdoc.user.domain.Doctor;
import org.springframework.data.domain.Page;

import java.util.List;

public record DoctorListResponse(
        List<DoctorListItemResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean hasNext
) {
    public static DoctorListResponse from(Page<Doctor> page) {
        return new DoctorListResponse(
                page.map(DoctorListItemResponse::from).getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext()
        );
    }
}