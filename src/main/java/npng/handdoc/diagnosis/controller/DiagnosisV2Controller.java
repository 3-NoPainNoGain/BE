package npng.handdoc.diagnosis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.dto.request.SignLogReq;
import npng.handdoc.diagnosis.dto.response.SummaryRes;
import npng.handdoc.diagnosis.service.DiagnosisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static npng.handdoc.global.response.ApiResponse.EMPTY_RESPONSE;

@RestController
@RequestMapping("/api/v2/diagnosis")
@Tag(name="Diagnosis2", description = "진료 관련 API V2")
@RequiredArgsConstructor
public class DiagnosisV2Controller {

    private final DiagnosisService diagnosisService;

    @PostMapping("/{diagnosisId}/sign")
    @Operation(summary = "텍스트로 변환된 수어 저장 API", description = "환자의 전송 버튼 클릭시 호출하는 API입니다. 진료 Id를 입력해주세요.")
    public ResponseEntity<Object> sign(@PathVariable String diagnosisId, @Valid @RequestBody SignLogReq request){
        diagnosisService.saveSignText(diagnosisId,request);
        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @GetMapping("/{diagnosisId}/summary")
    @Operation(summary = "진료를 요약하는 API", description = "진료 내용을 요약해줍니다. 진료 Id를 입력해주세요.")
    public ResponseEntity<SummaryRes> summary(@PathVariable String diagnosisId){
        SummaryRes summary = diagnosisService.getSummary(diagnosisId);
        return ResponseEntity.ok(summary);
    }
}
