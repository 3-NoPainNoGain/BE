package npng.handdoc.global.util.openai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import npng.handdoc.diagnosis.domain.ChatLog;
import npng.handdoc.diagnosis.domain.Diagnosis;
import npng.handdoc.diagnosis.dto.response.SummaryAIResponse;
import npng.handdoc.global.util.openai.OpenAIApiClient;
import npng.handdoc.global.util.openai.dto.Message;
import npng.handdoc.telemed.domain.TelemedChatLog;
import npng.handdoc.telemed.domain.type.Sender;
import npng.handdoc.telemed.dto.response.SpeechCandidateResponse;
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

    // 환자 발화 교정 + 후보 3개 생성
    public List<String> generateCandidates(String text){
        // 시스템 프롬프트
        String systemPrompt = """
                당신은 구음장애 환자의 발화를 '의미를 보존한 채' 가장 자연스러운 한국어 문장으로 정제하는 도우미입니다. 
                
                규칙: 
                - 의미 보존: 원문 의미를 절대로 변경하지 마세요. 
                - 과교정 금지: 문법/띄어쓰기/어휘를 '경미하게'만 수정하세요. 새로운 정보 추가나 삭제 절대 금지
                - 불확실 시 보수적: 의미가 모호하면 원문을 후보에 포함하세요. 
                - 출력: 반드시 아래 '예시'의 JSON '객체'만 출력하세요. 
                - 예시: {"candidates": ["문장1", "문장2", "문장3"]} 
                """;

        // 사용자 프롬프트
        String userPrompt = """
        원문: %s

        요구사항:
        1) 후보 3개를 만들어 주세요.
        2) 1번 후보: 원문과 가장 가까운 '보수적 교정'(띄어쓰기/어순/문법 최소 수정).
        3) 2~3번 후보: 같은 의미의 자연스러운 '경미한 바꿔 말하기'(의미 동일, 정보 불변).
        4) 의미가 확실치 않으면 해당 후보는 원문을 그대로 사용.

        오직 이 '객체'만 출력:
        {"candidates": ["...", "...", "..."]}
        """.formatted(text);

        List<Message> messages = List.of(
                new Message("system", systemPrompt),
                new Message("user", userPrompt)
        );

        String json = openAIApiClient.chatToJson(messages).block();

        try{
            SpeechCandidateResponse response = objectMapper.readValue(json, SpeechCandidateResponse.class);
            return response.candidates();
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 후보 응답 파싱 실패: " + json, e);
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
}
