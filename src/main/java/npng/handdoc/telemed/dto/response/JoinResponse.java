package npng.handdoc.telemed.dto.response;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.Option;
import npng.handdoc.telemed.domain.Telemed;
import npng.handdoc.telemed.domain.type.DiagnosisStatus;
import npng.handdoc.user.domain.type.Role;

import java.util.List;

public record JoinResponse(
        String roomId,
        Role role,
        DiagnosisStatus status,
        String wsUrl,
        List<IceServer> iceServers,
        List<Option> interpretationOption) {

    public static record IceServer(String urls){}

    public static JoinResponse from(Telemed telemed, Role role, String wsUrl, List<IceServer> iceServers, Reservation reservation) {
        return new JoinResponse(
                telemed.getId(),
                role,
                telemed.getDiagnosisStatus(),
                wsUrl,
                iceServers,
                reservation.getInterpretationOption().stream().toList()
        );
    }
}
