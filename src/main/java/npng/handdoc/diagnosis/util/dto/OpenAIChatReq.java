package npng.handdoc.diagnosis.util.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpenAIChatReq {
    @JsonProperty("model") private String model;
    @JsonProperty("max_tokens") private Integer max_tokens;
    @JsonProperty("temperature") private Double temperature;
    @JsonProperty("top_p") private Double top_p;
    @JsonProperty("messages") private List<Message> messages;
    @JsonProperty("response_format") private Object responseFormat;
}
