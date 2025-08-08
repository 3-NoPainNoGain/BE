package npng.handdoc.diagnosis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import npng.handdoc.diagnosis.domain.type.MessageType;
import npng.handdoc.diagnosis.domain.type.Sender;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLog {

    private Sender sender;
    private MessageType messageType;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
