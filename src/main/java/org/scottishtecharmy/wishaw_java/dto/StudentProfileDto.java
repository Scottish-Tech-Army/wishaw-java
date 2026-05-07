package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileDto {
    private long studentId;
    private String username;
    private String gamertag;
    private String bio;
    private String avatarUrl;
}
