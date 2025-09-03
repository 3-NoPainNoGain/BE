package npng.handdoc.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.user.service.DoctorService;
import npng.handdoc.global.response.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name="Doctor", description = "의사 관련 API")
@RequestMapping("/api/v2/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary="의사 목록 조회 API", description = "의사 목록 전체를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<Object>> getDoctorList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.from(doctorService.getDoctorList(pageable)));
    }

    @Operation(summary = "의사 상세 조회 API", description = "의사에 대한 상세 조회를 제공합니다. 의사 id를 입력해주세요.")
    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Object>> getDoctorDetail(@PathVariable Long doctorId){
        return ResponseEntity.ok(ApiResponse.from(doctorService.getDoctorDetail(doctorId)));
    }
}