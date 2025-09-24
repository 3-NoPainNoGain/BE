package npng.handdoc.telemed.dto.response;

import java.time.LocalDateTime;

public record ChatResponse(
        String sender,
        String message,
        LocalDateTime timestamp) {}
