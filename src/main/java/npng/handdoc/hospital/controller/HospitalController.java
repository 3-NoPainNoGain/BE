package npng.handdoc.hospital.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.hospital.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "위경도 기반 병원 데이터 조회 API", description = "위경도를 입력하여 근처의 병원을 조회합니다. 예시는 이화여자대학교입니다.")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<Object>> nearbyHospitals(
            @RequestParam(value = "latitude", defaultValue = "37.5619") Double latitude,
            @RequestParam(value = "longitude", defaultValue = "126.9472") Double longitude,
            @RequestParam(value="radiusKm", defaultValue="3") Double radiusKm){
        return ResponseEntity.ok(ApiResponse.from(hospitalService.findNearByHospitals(latitude, longitude, radiusKm)));
    }
}
