package npng.handdoc.global.util.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;
}
