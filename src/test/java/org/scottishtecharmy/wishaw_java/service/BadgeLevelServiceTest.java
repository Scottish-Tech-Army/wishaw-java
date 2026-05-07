package org.scottishtecharmy.wishaw_java.service;

import org.junit.jupiter.api.Test;
import org.scottishtecharmy.wishaw_java.enums.BadgeLevel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadgeLevelServiceTest {

    private final BadgeLevelService badgeLevelService = new BadgeLevelService();

    @Test
    void resolvesExpectedThresholds() {
        assertEquals(BadgeLevel.NONE, badgeLevelService.resolve(0));
        assertEquals(BadgeLevel.BRONZE, badgeLevelService.resolve(1));
        assertEquals(BadgeLevel.SILVER, badgeLevelService.resolve(31));
        assertEquals(BadgeLevel.GOLD, badgeLevelService.resolve(71));
        assertEquals(BadgeLevel.PLATINUM, badgeLevelService.resolve(121));
    }

    @Test
    void returnsFrontendFriendlyLabels() {
        assertEquals("None", badgeLevelService.resolveLabel(0));
        assertEquals("Gold", badgeLevelService.resolveLabel(80));
    }
}
