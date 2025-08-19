package npng.handdoc.diagnosis.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaCsrRes(
        @JsonProperty("text") String text) {}
