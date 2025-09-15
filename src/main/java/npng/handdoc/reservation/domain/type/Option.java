package npng.handdoc.reservation.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Option {
    SIGN_TO_TEXT("수어 텍스트 변환"),
    VOICE_TO_TEXT("음성 텍스트 변환");

    private final String label;

    public String getLabel() {
        return label;
    }
    }

