package npng.handdoc.telemed.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record HistoryListResponse(
        List<HistoryItemResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean hasNext
) {
    public static HistoryListResponse from(Page<HistoryItemResponse> page){
        return new HistoryListResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext()
        );
    }
}
