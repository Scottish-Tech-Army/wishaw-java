package org.scottishtecharmy.wishaw.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "module_detail")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ModuleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "week_no", nullable = false)
    private int weekNo;

    @Column(name = "session_focus_description", length = 2000)
    private String sessionFocusDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_badge_id")
    private SubBadge subBadge;

    @Column(name = "session_plan_link")
    private String sessionPlanLink;

    @Column(name = "session_slide_link")
    private String sessionSlideLink;
}
