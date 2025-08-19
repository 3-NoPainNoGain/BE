package npng.handdoc.speech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.speech.client.NaverCsrClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Speech", description = "음성 관련 API")
public class SpeechController {

    private final NaverCsrClient naverCsrClient;

    @PostMapping(name = "api/v1/diagnosis/{diagnosisId}/stt",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "음성을 텍스트로 변환하고 DB에 저장하는 API", description = "음성 파일을 업로드하여 호출합니다.")
    public ResponseEntity<String> stt(@PathVariable String diagnosisId,
                                      @RequestPart("file") MultipartFile file) throws Exception{
        String result = naverCsrClient.transcribe(file.getBytes());
        return ResponseEntity.ok(result);
    }
}
