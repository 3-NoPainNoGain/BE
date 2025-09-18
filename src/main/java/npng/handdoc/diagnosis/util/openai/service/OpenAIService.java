package npng.handdoc.diagnosis.util.openai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.ChatLog;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.dto.response.SummaryAIResponse;
import npng.handdoc.diagnosis.util.openai.OpenAIApiClient;
import npng.handdoc.diagnosis.util.openai.dto.Message;
import npng.handdoc.telemed.domain.TelemedChatLog;
import npng.handdoc.telemed.domain.type.Sender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final OpenAIApiClient openAIApiClient;
    private final ObjectMapper objectMapper;

    // 대면 요약
    public SummaryAIResponse summarize(Diagnosis diagnosis) {
        List<Message> messages = buildMessage(diagnosis.getChatLogList());
        String json = openAIApiClient.chatToJson(messages).block();
        try {
            return objectMapper.readValue(json, SummaryAIResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패: " + json, e);
        }
    }

    // 비대면 요약
    public SummaryAIResponse summarize(TelemedChatLog telemedChatLog) {
        List<Message> messages = buildMessageFromTelemed(
                telemedChatLog == null ? null : telemedChatLog.getMessageList()
        );
        String json = openAIApiClient.chatToJson(messages).block();
        try {
            return objectMapper.readValue(json, SummaryAIResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패: " + json, e);
        }
    }

    private List<Message> buildMessage(List<ChatLog> logs){
        List<ChatLog> safe = logs == null ? List.of() : logs.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String transcript = safe.isEmpty()
                ? "(대화 기록이 없습니다.)"
                : safe.stream()
                .map(l -> {
                    String who = (l.getSender() == null) ? "알수없음" : l.getSender().getLabel();
                    String msg = l.getMessage() == null ? "" : l.getMessage();
                    String ts  = l.getTimestamp() == null ? "-" : fmt.format(l.getTimestamp());
                    return who + ": " + msg + " (" + ts + ")";
                })
                .collect(Collectors.joining("\n"));

        String system = baseSystemPrompt();

        return List.of(
                new Message("system", system),
                new Message("user", "대화 원문:\n" + transcript)
        );
    }

    private List<Message> buildMessageFromTelemed(List<TelemedChatLog.Message> logs) {
        List<TelemedChatLog.Message> safe = logs == null ? List.of() : logs.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String transcript = safe.isEmpty()
                ? "(대화 기록이 없습니다.)"
                : safe.stream()
                .map(l -> {
                    String who = (l.getSender() == null) ? "알수없음" : l.getSender().getLabel();
                    String msg = l.getMessage() == null ? "" : l.getMessage();
                    String ts  = l.getTimestamp() == null ? "-" : fmt.format(l.getTimestamp());
                    return who + ": " + msg + " (" + ts + ")";
                })
                .collect(Collectors.joining("\n"));

        String system = baseSystemPrompt();

        return List.of(
                new Message("system", system),
                new Message("user", "대화 원문:\n" + transcript)
        );
    }

    private String baseSystemPrompt() {
        return """
                당신은 의무기록 요약 도우미입니다.
                아래 '의사–환자' 대화를 읽고 JSON **객체 하나만** 출력하세요.
                키는 정확히 다음 3개입니다:
                - "symptom": 대표 증상 (짧게)
                - "impression": 의사의 소견/진단 (짧게)
                - "prescription": 처방/계획 (콤마 구분)

                규칙:
                - 반드시 JSON만 출력.
                - 한국어로 작성.
                - 대화에 없는 정보는 "없음".
                """;
    }

    private String labelOf(Sender sender) {
        return switch (sender) {
            case DOCTOR -> "의사";
            case PATIENT -> "환자";
        };
    }
}
