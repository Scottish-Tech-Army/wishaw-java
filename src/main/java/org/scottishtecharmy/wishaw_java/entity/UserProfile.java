package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private String userId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    private String displayName;
    private String firstName;
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String bio;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String photoUrl;

    private String overlayTemplate;
    private boolean showInPublicList;
    private boolean allowSocialSharing;
}
