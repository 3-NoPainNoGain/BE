package npng.handdoc.telemed.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import npng.handdoc.global.entity.BaseDocument;
import npng.handdoc.telemed.domain.type.MessageType;
import npng.handdoc.telemed.domain.type.Sender;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "telemed_chat_logs")
public class TelemedChatLog extends BaseDocument {

    @Id
    private String id;

    // roomId = Telemed.id (UUID) 문자열
    private String roomId;
    List<Message> messageList;

    @Data
    @Builder
    public static class Message {
        private Sender sender;
        private MessageType messageType;
        private String message;

        @Builder.Default
        private LocalDateTime timestamp = LocalDateTime.now();
    }
}
