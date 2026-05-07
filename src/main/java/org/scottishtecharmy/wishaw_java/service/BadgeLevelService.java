package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.enums.BadgeLevel;
import org.springframework.stereotype.Service;

@Service
public class BadgeLevelService {

    public BadgeLevel resolve(int points) {
        if (points >= 121) {
            return BadgeLevel.PLATINUM;
        }
        if (points >= 71) {
            return BadgeLevel.GOLD;
        }
        if (points >= 31) {
            return BadgeLevel.SILVER;
        }
        if (points >= 1) {
            return BadgeLevel.BRONZE;
        }
        return BadgeLevel.NONE;
    }

    public String resolveLabel(int points) {
        return switch (resolve(points)) {
            case NONE -> "None";
            case BRONZE -> "Bronze";
            case SILVER -> "Silver";
            case GOLD -> "Gold";
            case PLATINUM -> "Platinum";
        };
    }
}
