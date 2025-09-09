package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public record EndResponse(
        String roomId,
        DiagnosisStatus status,
        LocalDateTime endedAt,
        String duration
) {
    public static EndResponse from(Telemed telemed){
        String duration = null;
        if (telemed.getStartedAt() != null && telemed.getEndedAt() != null) {
            Duration d = Duration.between(telemed.getStartedAt(), telemed.getEndedAt());
            long seconds = d.getSeconds();
            long h = seconds / 3600;
            long m = (seconds % 3600) / 60;
            long s = seconds % 60;
            duration = String.format("%02d:%02d:%02d", h, m, s);
        }
        return new EndResponse(
                telemed.getId(),
                telemed.getDiagnosisStatus(),
                telemed.getEndedAt(),
                duration
        );
    }
}
