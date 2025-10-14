package npng.handdoc.hospital.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class HospitalException extends RuntimeException {
  private final ErrorCode errorCode;
}
