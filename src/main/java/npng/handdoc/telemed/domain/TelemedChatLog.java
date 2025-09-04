package npng.handdoc.telemed.domain;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import npng.handdoc.global.entity.BaseDocument;
import npng.handdoc.telemed.domain.type.MessageType;
import npng.handdoc.telemed.domain.type.Sender;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "telemed_chat_logs")
public class TelemedChatLog extends BaseDocument {

    @Id
    private String id;

    // roomId = Telemed.id (UUID) 문자열
    private String roomId;

    private Sender sender;
    private MessageType messageType;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
