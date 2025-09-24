package npng.handdoc.telemed.dto.response;

import java.util.List;

public record ChatListResponse(
        String roomId,
        List<ChatResponse> messages) {}
