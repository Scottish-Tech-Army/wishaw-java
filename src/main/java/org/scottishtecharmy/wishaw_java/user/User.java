package org.scottishtecharmy.wishaw_java.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.common.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    private String displayName;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "centre_id")
    private Centre centre;

    @Column(length = 1000)
    private String profileImageUrl;
}
