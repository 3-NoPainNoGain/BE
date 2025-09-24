package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.Summary;
import npng.handdoc.telemed.domain.TelemedChatLog;

public record HistoryDetailResponse(
        ChatListResponse chat,
        SummaryResponse summary
) {
    public static HistoryDetailResponse from(TelemedChatLog chatLog, Summary summary) {
        ChatListResponse chatListResponse = new ChatListResponse(
                chatLog.getRoomId(),
                chatLog.getMessageList().stream()
                        .map(m -> new ChatResponse(
                                m.getSender(),
                                m.getMessage(),
                                m.getTimestamp()
                        )).toList()
        );

        SummaryResponse summaryResponse = SummaryResponse.from(summary);
        return new HistoryDetailResponse(chatListResponse, summaryResponse);
    }
}
