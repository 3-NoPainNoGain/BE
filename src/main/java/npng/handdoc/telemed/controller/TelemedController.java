package npng.handdoc.telemed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.telemed.service.TelemedService;
import npng.handdoc.user.util.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Telemed", description = "비대면 관련 API")
@RequestMapping("/api/v2/telemed")
public class TelemedController {

    private final TelemedService telemedService;

    @Operation(summary = "(환자, 의사) 진료실 입장 API", description = "진료 입장 버튼에 사용되는 API입니다. 의사와 환자 공통으로 사용합니다.")
    @PostMapping("/{reservationId}/join")
    public ResponseEntity<ApiResponse<Object>> join(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.from(telemedService.join(userDetails.getId(), reservationId)));
    }

    @Operation(summary = "(환자, 의사) 진료실 종료 API", description = "진료 종료 버튼을 통해 화상 통화를 마무리합니다.")
    @PostMapping("/{roomId}/end")
    public ResponseEntity<ApiResponse<Object>> end(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable String roomId) {
        return ResponseEntity.ok(ApiResponse.from(telemedService.end(userDetails.getId(), roomId)));
    }
}
