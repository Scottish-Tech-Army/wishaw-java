package org.scottishtecharmy.wishaw_java.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * // FRONTEND_INTEGRATION: React import wizard sends player column -> user ID mappings here.
 */
@Getter
@Setter
public class ImportPlayerMappingRequest {
    // Map of CSV player identifier -> existing player user ID
    private Map<String, Long> playerMappings;
}
