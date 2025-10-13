package npng.handdoc.hospital.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.hospital.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Map", description = "지도 API")
@RequestMapping("/api/v3/map")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(summary = "(프론트 연동 X) 공공데이터 적재 API", description = "공공데이터를 호출하여 db에 적재합니다.")
    @PostMapping("/insert")
    public ResponseEntity<ApiResponse<Object>> insertHospitals(){
        hospitalService.synchronizeHospitalData();
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }
}
