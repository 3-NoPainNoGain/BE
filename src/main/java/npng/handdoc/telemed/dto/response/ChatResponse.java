package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.type.Sender;

import java.time.LocalDateTime;

public record ChatResponse(
        Sender sender,
        String message,
        LocalDateTime timestamp) {}
