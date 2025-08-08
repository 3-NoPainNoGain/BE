package npng.handdoc.diagnosis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.dto.response.StartDiagnosisRes;
import npng.handdoc.diagnosis.service.DiagnosisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name="Diagnosis", description = "진료 관련 API")
@RequestMapping("/api/v1/diagnosis")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping("/start")
    @Operation(summary = "진료 세션 시작 API", description = "진료 시작 버튼을 눌렀을 때 호출하는 API입니다. 새로운 진료 Id를 발급하며, 생성된 진료는 1일(24시간) 후 자동 만료됩니다.")
    public ResponseEntity<StartDiagnosisRes> start(){
        Diagnosis diagnosis = diagnosisService.startDiagnosis();
        return ResponseEntity.ok(StartDiagnosisRes.of(diagnosis.getId()));
    }
}
