package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * User info returned after creating/listing users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDto {
    private Long id;
    private String username;
    private String name;
    private String gamertag;
    private String centre;
    private String group;
    private int level;
    private int totalXP;
    private int badgesEarned;
    private String joinedDate;
    private String avatarUrl;
}
