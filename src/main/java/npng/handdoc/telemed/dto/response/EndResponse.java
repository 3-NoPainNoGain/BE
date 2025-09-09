package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;

import java.time.LocalDateTime;

public record EndResponse(
        String roomId,
        DiagnosisStatus status,
        LocalDateTime endedAt
) {
    public static EndResponse from(Telemed telemed){
        return new EndResponse(
                telemed.getId(),
                telemed.getDiagnosisStatus(),
                telemed.getEndedAt()
        );
    }
}
