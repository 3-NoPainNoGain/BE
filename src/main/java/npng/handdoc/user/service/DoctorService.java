package npng.handdoc.user.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.user.domain.DoctorProfile;
import npng.handdoc.user.dto.response.DoctorDetailResponse;
import npng.handdoc.user.dto.response.DoctorListResponse;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.exception.errorcode.UserErrorCode;
import npng.handdoc.user.repository.DoctorProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorListResponse getDoctorList(Pageable pageable){
        Page<DoctorProfile> doctors = doctorProfileRepository.findAll(pageable);
        return DoctorListResponse.from(doctors);
    }

    public DoctorDetailResponse getDoctorDetail(Long doctorId){
        DoctorProfile doctorProfile = findDoctorOrThrow(doctorId);
        return DoctorDetailResponse.from(doctorProfile);
    }

    @Transactional
    public void addTag(Long userId, String tagName){
        DoctorProfile doctorProfile = findDoctorOrThrowByUserId(userId);
        doctorProfile.addTag(tagName);
    }

    private DoctorProfile findDoctorOrThrow(Long doctorId){
        return doctorProfileRepository.findById(doctorId)
                .orElseThrow(()-> new UserException(UserErrorCode.DOCTOR_NOT_FOUND));
    }

    private DoctorProfile findDoctorOrThrowByUserId(Long userId){
        return doctorProfileRepository.findByUserId(userId)
                .orElseThrow(()-> new UserException(UserErrorCode.DOCTOR_NOT_FOUND));
    }
}
