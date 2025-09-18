package npng.handdoc.diagnosis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.dto.request.SignLogRequest;
import npng.handdoc.diagnosis.dto.response.StartDiagnosisResponse;
import npng.handdoc.diagnosis.dto.response.SttResultResponse;
import npng.handdoc.diagnosis.dto.response.SummaryResponse;
import npng.handdoc.diagnosis.service.DiagnosisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static npng.handdoc.global.response.ApiResponse.EMPTY_RESPONSE;

@RestController
@RequiredArgsConstructor
@Tag(name="Diagnosis", description = "진료 관련 API")
@RequestMapping("/api/v1/diagnosis")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping("/start")
    @Operation(summary = "진료 세션 시작 API", description = "진료 시작 버튼을 눌렀을 때 호출하는 API입니다. 새로운 진료 Id를 발급하며, 생성된 진료는 1일(24시간) 후 자동 만료됩니다.")
    public ResponseEntity<StartDiagnosisResponse> start(){
        Diagnosis diagnosis = diagnosisService.startDiagnosis();
        return ResponseEntity.ok(StartDiagnosisResponse.of(diagnosis.getId()));
    }

    @PatchMapping("/{diagnosisId}/end")
    @Operation(summary = "진료 세션 종료 API", description = "진료 종료 버튼을 눌렀을 때 호출하는 API입니다. 진료 Id를 입력해주세요.")
    public ResponseEntity<Object> end(@PathVariable String diagnosisId){
        diagnosisService.endDiagnosis(diagnosisId);
        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @PostMapping("/{diagnosisId}/sign")
    @Operation(summary = "텍스트로 변환된 수어 저장 API", description = "환자의 전송 버튼 클릭시 호출하는 API입니다. 진료 Id를 입력해주세요.")
    public ResponseEntity<Object> sign(@PathVariable String diagnosisId, @Valid @RequestBody SignLogRequest request){
        diagnosisService.saveSignText(diagnosisId,request);
        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @PostMapping(value = "/{diagnosisId}/speech", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "음성을 텍스트로 변환하고 DB에 저장하는 API", description = "음성 파일을 업로드하여 호출합니다.")
    public ResponseEntity<SttResultResponse> stt(@PathVariable String diagnosisId,
                                                 @RequestPart("file") MultipartFile file) throws Exception{
        String result = diagnosisService.saveSpeechText(diagnosisId, file);
        return ResponseEntity.ok(SttResultResponse.of(result));
    }

    @GetMapping("/{diagnosisId}/summary")
    @Operation(summary = "진료를 요약하는 API", description = "진료 내용을 요약해줍니다. 진료 Id를 입력해주세요.")
    public ResponseEntity<SummaryResponse> summary(@PathVariable String diagnosisId){
        SummaryResponse summary = diagnosisService.getSummary(diagnosisId);
        return ResponseEntity.ok(summary);
    }
}
