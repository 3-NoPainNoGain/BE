package npng.handdoc.diagnosis.util.openai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import npng.handdoc.diagnosis.util.openai.dto.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAIApiClient {

    private final WebClient webClient;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final long TIMEOUT_SECONDS = 15;

    @Value("${openai.api.key}")
    private String apiKey;

    public Mono<String> chatToJson(List<Message> messages) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", DEFAULT_MODEL);
        body.put("temperature", 0.2);
        body.put("response_format", Map.of("type", "json_object")); // JSON 강제
        body.put("messages", messages);

        return webClient.post()
                .uri(OPENAI_API_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(msg -> {
                                    log.error("OpenAI error: status={}, body={}", resp.statusCode(), msg);
                                    return Mono.error(new IllegalStateException("OpenAI Error: " + msg));
                                }))
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .map(res -> {
                    var choices = (List<Map<String, Object>>) res.get("choices");
                    if (choices == null || choices.isEmpty()) {
                        throw new IllegalStateException("OpenAI Error: empty choices");
                    }
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) msg.get("content");
                    if (content == null || content.isBlank()) {
                        throw new IllegalStateException("OpenAI Error: empty content");
                    }
                    return content;
                });
    }
}
