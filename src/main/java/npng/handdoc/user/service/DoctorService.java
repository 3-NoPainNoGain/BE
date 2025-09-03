package npng.handdoc.user.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.exception.DiagnosisException;
import npng.handdoc.diagnosis.exception.errorcode.DiagnosisErrorCode;
import npng.handdoc.user.domain.Doctor;
import npng.handdoc.user.dto.response.DoctorDetailResponse;
import npng.handdoc.user.dto.response.DoctorListResponse;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.exception.errorcode.UserErrorCode;
import npng.handdoc.user.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorListResponse getDoctorList(Pageable pageable){
        Page<Doctor> doctors = doctorRepository.findAll(pageable);
        return DoctorListResponse.from(doctors);
    }

    public DoctorDetailResponse getDoctorDetail(@PathVariable Long doctorId){
        Doctor doctor = findDoctorOrThrow(doctorId);
        return DoctorDetailResponse.from(doctor);
    }

    private Doctor findDoctorOrThrow(Long doctorId){
        return doctorRepository.findById(doctorId)
                .orElseThrow(()-> new UserException(UserErrorCode.DOCTOR_NOT_FOUND));
    }
}
