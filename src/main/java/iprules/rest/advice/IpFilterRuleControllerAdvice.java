package iprules.rest.advice;

import iprules.rest.dto.ErrorResponseDTO;
import iprules.service.exception.IpAddressValidatorException;
import iprules.service.exception.IpRuleMissingException;
import iprules.service.exception.SqlErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class IpFilterRuleControllerAdvice {

    @ExceptionHandler({IpAddressValidatorException.class})
    public ResponseEntity<ErrorResponseDTO> invalidIpAddress(Throwable err) {
        return ResponseEntity.badRequest().body(
                ErrorResponseDTO.builder()
                        .message(err.getMessage())
                        .status(400)
                        .build()
        );
    }

    @ExceptionHandler({IpRuleMissingException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void ipRuleMissing() {
    }

    @ExceptionHandler({SqlErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void exceptionThrownByIsAllowedQuery() {
    }
}