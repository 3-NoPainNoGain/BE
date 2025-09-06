package npng.handdoc.user.dto.response;

import npng.handdoc.user.domain.DoctorTag;

public record DoctorTagResponse(Long id, String name) {
    public static DoctorTagResponse from(DoctorTag tag) {
        return new DoctorTagResponse(tag.getId(), tag.getName());
    }
}