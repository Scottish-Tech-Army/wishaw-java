package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.model.Student;
import org.scottishtecharmy.wishaw_java.model.XpEvent;
import org.scottishtecharmy.wishaw_java.repository.StudentRepository;
import org.scottishtecharmy.wishaw_java.repository.XpEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Centralised service for awarding XP, recording activity events,
 * and handling level-up logic.
 *
 * Level thresholds use the formula: XP needed = level * 200
 *   Level 1 →   0 XP
 *   Level 2 → 200 XP
 *   Level 3 → 400 XP  etc.
 */
@Service
public class XpService {

    private final StudentRepository studentRepository;
    private final XpEventRepository xpEventRepository;

    public XpService(StudentRepository studentRepository,
                     XpEventRepository xpEventRepository) {
        this.studentRepository = studentRepository;
        this.xpEventRepository = xpEventRepository;
    }

    /**
     * Award XP to a student, record an XpEvent, and level-up if applicable.
     *
     * @param student  the student entity (will be mutated and saved)
     * @param xp       amount of XP to award
     * @param activity human-readable description shown in recent activity
     * @param icon     emoji icon for the activity feed
     */
    @Transactional
    public void awardXp(Student student, int xp, String activity, String icon) {
        // 1. Update student's total XP
        student.setXp(student.getXp() + xp);

        // 2. Check for level-up(s)
        int newLevel = calculateLevel(student.getXp());
        boolean levelledUp = newLevel > student.getLevel();
        student.setLevel(newLevel);
        studentRepository.save(student);

        // 3. Record the XP event
        xpEventRepository.save(XpEvent.builder()
                .student(student)
                .activity(activity)
                .xp(xp)
                .date(today())
                .icon(icon)
                .build());

        // 4. If levelled up, record a bonus event
        if (levelledUp) {
            xpEventRepository.save(XpEvent.builder()
                    .student(student)
                    .activity("Reached Level " + newLevel + "!")
                    .xp(0)
                    .date(today())
                    .icon("⭐")
                    .build());
        }
    }

    /**
     * Calculate the level for a given total XP.
     * Each level requires (level * 200) cumulative XP.
     */
    public int calculateLevel(int totalXp) {
        int level = 1;
        while ((level + 1) * 200 <= totalXp) {
            level++;
        }
        return Math.max(level, 1);
    }

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
