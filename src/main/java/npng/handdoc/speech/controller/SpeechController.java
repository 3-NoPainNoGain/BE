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
@RequestMapping("/api/v1/stt")
public class SpeechController {

    private final NaverCsrClient naverCsrClient;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "음성을 텍스트로 변환하는 API", description = "음성 파일을 업로드하여 호출합니다.")
    public ResponseEntity<String> stt(@RequestPart("file") MultipartFile file) throws Exception{
        String result = naverCsrClient.transcribe(file.getBytes());
        return ResponseEntity.ok(result);
    }
}
