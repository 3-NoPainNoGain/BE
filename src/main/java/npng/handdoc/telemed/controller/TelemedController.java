package npng.handdoc.telemed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.dto.response.SummaryResponse;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.telemed.dto.request.SendSpeechTextRequest;
import npng.handdoc.telemed.dto.request.SignRequest;
import npng.handdoc.telemed.service.TelemedChatService;
import npng.handdoc.telemed.service.TelemedService;
import npng.handdoc.user.util.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Telemed", description = "비대면 관련 API")
@RequestMapping("/api/v2/telemed")
public class TelemedController {

    private final TelemedService telemedService;
    private final TelemedChatService telemedChatService;

    @Operation(summary = "(환자, 의사) 진료실 입장 API", description = "진료 입장 버튼에 사용되는 API입니다. 의사와 환자 공통으로 사용합니다.")
    @PostMapping("/{reservationId}/join")
    public ResponseEntity<ApiResponse<Object>> join(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.from(telemedService.join(userDetails.getId(), reservationId)));
    }

    @Operation(summary = "(환자, 의사) 진료실 종료 API", description = "진료 종료 버튼을 통해 화상 통화를 마무리합니다. roomId를 입력해주세요.")
    @PostMapping("/{roomId}/end")
    public ResponseEntity<ApiResponse<Object>> end(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable String roomId) {
        return ResponseEntity.ok(ApiResponse.from(telemedService.end(userDetails.getId(), roomId)));
    }

    @Operation(summary = "(환자) 수어 텍스트 전송하고 db에 저장하는 API", description = "환자의 수어 텍스트를 db에 저장합니다.")
    @PostMapping("/{roomId}/sign")
    public ResponseEntity<ApiResponse<Object>> sign(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable String roomId,
                                                    @RequestBody SignRequest request) {
        telemedChatService.saveSign(userDetails.getId(), roomId, request);
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "(의사) 음성을 텍스트로 변환하고 db에 저장하는 API", description = "의사의 음성 텍스트를 db에 저장합니다.")
    @PostMapping(value = "/{roomId}/speech-doctor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> speechDoctor(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PathVariable String roomId,
                                                      @RequestPart("file") MultipartFile file)throws Exception{
        String result = telemedChatService.saveDoctorSpeechText(userDetails.getId(), roomId, file);
        return ResponseEntity.ok(ApiResponse.from(result));
    }

    @Operation(summary = "(환자) 음성을 텍스트로 변환하고 db에 저장하는 API", description = "환자의 음성 텍스트를 db에 저장합니다.")
    @PostMapping(value = "/{roomId}/speech-patient-normal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> speechPatientNormal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                   @PathVariable String roomId,
                                                                   @RequestPart("file") MultipartFile file)throws Exception{
        String result = telemedChatService.savePatientNormalSpeechText(userDetails.getId(), roomId, file);
        return ResponseEntity.ok(ApiResponse.from(result));
    }

    @Operation(summary = "(환자) 음성을 텍스트로 변환하여 3가지 선택을 제공하는 API", description = "환자의 음성을 녹음하여 전송합니다.")
    @PostMapping(value = "/{roomId}/speech-patient",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> speechPatient(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @PathVariable String roomId,
                                                             @RequestPart("file") MultipartFile file)throws Exception{
        return ResponseEntity.ok(ApiResponse.from(telemedChatService.getSpeechText(userDetails.getId(), roomId, file)));
    }

    @Operation(summary = "(환자) 선택한 3가지 중 하나를 선택해 의사에게 전송하는 API", description = "한 가지를 의사에게 전달하고 db에 저장합니다.")
    @PostMapping("/{roomId}/speech-patient/send")
    public ResponseEntity<ApiResponse<Object>> sendSpeech(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable String roomId,
                                                          @RequestBody SendSpeechTextRequest request) {
        telemedChatService.savePatientSpeechText(userDetails.getId(), roomId, request.selectedText());
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "비대면 진료 요약 API", description = "비대면 진료가 종료된 다음 해당 진료의 내용을 요약합니다. roomId를 입력하세요.")
    @GetMapping("/{roomId}/summary")
    public ResponseEntity<SummaryResponse> summary(@PathVariable String roomId){
        SummaryResponse summary = telemedChatService.saveSummary(roomId);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "비대면 진료 내역 조회 API", description = "비대면 진료 내역을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Object>> history(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.from(telemedService.getHistory(pageable, userDetails.getId())));
    }

    @Operation(summary = "비대면 진료 내역 상세 조회 API", description = "비대면 진료에서 의사와 환자의 대화 내용과 요약을 조회합니다.")
    @GetMapping("/history/{roomId}")
    public ResponseEntity<ApiResponse<Object>> historyDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String roomId){
        return ResponseEntity.ok(ApiResponse.from(telemedService.getHistoryDetail(userDetails.getId(), roomId)));
    }
}
