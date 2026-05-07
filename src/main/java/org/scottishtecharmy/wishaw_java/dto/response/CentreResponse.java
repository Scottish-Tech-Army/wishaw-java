package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CentreResponse {
    private Long id;
    private String name;
    private String code;
    private boolean active;
}
