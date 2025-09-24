package npng.handdoc.global.util.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaCsrRes(
        @JsonProperty("text") String text) {}
