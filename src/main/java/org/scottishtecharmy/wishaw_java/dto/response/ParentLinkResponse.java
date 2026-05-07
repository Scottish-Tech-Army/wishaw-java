package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentLinkResponse {
    private Long id;
    private Long parentUserId;
    private String parentDisplayName;
    private Long playerUserId;
    private String playerDisplayName;
    private String relationshipLabel;
}
