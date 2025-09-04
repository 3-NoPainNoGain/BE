package npng.handdoc.webrtc.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import npng.handdoc.global.entity.BaseDocument;
import npng.handdoc.webrtc.domain.type.MessageType;
import npng.handdoc.webrtc.domain.type.Sender;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLog extends BaseDocument {
    @Id
    private String id;

    private Sender sender;
    private MessageType messageType;
    private String message;
}
