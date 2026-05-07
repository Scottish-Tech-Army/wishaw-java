package org.scottishtecharmy.wishaw_java.exception;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String path,
        String correlationId,
        Map<String, String> fieldErrors
) {
}
