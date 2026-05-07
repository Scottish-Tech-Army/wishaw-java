package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    /** Hint returned by POST /auth/forgot-password */
    private String passwordHint;

    @Column(nullable = false)
    private String gamertag;

    @Column(nullable = false)
    private String realName;

    @Column(length = 1000)
    private String bio;

    private String avatarUrl;

    @Column(unique = true)
    private String email;

    /** ROLE_STUDENT or ROLE_ADMIN */
    @Column(nullable = false)
    private String role;

    private int level;

    private int xp;

    /** ISO-8601 date string, e.g. "Sep 2024" */
    private String joinedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_id")
    private Centre centre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private boolean captain;
}
