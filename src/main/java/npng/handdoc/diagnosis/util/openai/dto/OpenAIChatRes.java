package npng.handdoc.diagnosis.util.openai.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIChatRes {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private ChatMessage message;
        private String finish_reason;
        private int index;
    }

    @Data
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
