package npng.handdoc.speech.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class NaverCsrClient {

    private final WebClient webClient;

    @Value("${naver.clova.api-key-id}")
    private String apiKeyId;

    @Value("${naver.clova.api-key}")
    private String apiKey;

    private static final String DEFAULT_LANG = "Kor";

    public String transcribe(byte[] audioBytes){
        return webClient.post()
                .uri(uri -> uri.scheme("https")
                        .host("naveropenapi.apigw.ntruss.com")
                        .path("/recog/v1/stt")
                        .queryParam("lang", DEFAULT_LANG)
                        .build())
                .header("X-NCP-APIGW-API-KEY-ID", apiKeyId)
                .header("X-NCP-APIGW-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(audioBytes)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(msg-> Mono.error(new IllegalStateException("Clova CSR Error: " + msg))))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(20))
                .block();
    }
}
