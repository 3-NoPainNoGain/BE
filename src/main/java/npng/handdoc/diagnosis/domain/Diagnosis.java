package npng.handdoc.diagnosis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import npng.handdoc.global.entity.BaseDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diagnoses")
public class Diagnosis extends BaseDocument {

    @Id
    private String id;

    private List<ChatLog> chatLogList = new ArrayList<>();

    @Indexed(name = "expiredAt")
    private LocalDateTime expiredAt;

    public void addChatLog(ChatLog chatLog) {
        this.chatLogList.add(chatLog);
    }
}
