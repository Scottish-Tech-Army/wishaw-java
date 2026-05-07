package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.entity.Sport;
import org.scottishtecharmy.wishaw_java.entity.UserProfile;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

@Service
public class AgePolicyService {

    public LocalDate parseDateOfBirth(String rawValue, boolean required) {
        if (rawValue == null || rawValue.isBlank()) {
            if (required) {
                throw new BadRequestException("Date of birth is required");
            }
            return null;
        }

        try {
            LocalDate dateOfBirth = LocalDate.parse(rawValue);
            if (dateOfBirth.isAfter(LocalDate.now(ZoneOffset.UTC))) {
                throw new BadRequestException("Date of birth cannot be in the future");
            }
            return dateOfBirth;
        } catch (DateTimeParseException exception) {
            throw new BadRequestException("Date of birth must use YYYY-MM-DD format");
        }
    }

    public void validateSportAgeRange(Integer minAge, Integer maxAge) {
        if (minAge != null && minAge < 0) {
            throw new BadRequestException("Minimum age cannot be negative");
        }
        if (maxAge != null && maxAge < 0) {
            throw new BadRequestException("Maximum age cannot be negative");
        }
        if (minAge != null && maxAge != null && minAge > maxAge) {
            throw new BadRequestException("Minimum age cannot be greater than maximum age");
        }
    }

    public Integer calculateAge(LocalDate dateOfBirth, LocalDate referenceDate) {
        if (dateOfBirth == null) {
            return null;
        }

        LocalDate effectiveReferenceDate = referenceDate == null ? LocalDate.now(ZoneOffset.UTC) : referenceDate;
        if (dateOfBirth.isAfter(effectiveReferenceDate)) {
            return 0;
        }

        return Period.between(dateOfBirth, effectiveReferenceDate).getYears();
    }

    public void validateSportEligibility(Sport sport, UserProfile userProfile, LocalDate referenceDate) {
        if (sport == null || (sport.getMinAge() == null && sport.getMaxAge() == null)) {
            return;
        }

        if (userProfile == null || userProfile.getDateOfBirth() == null) {
            throw new BadRequestException("Date of birth is required to join age-restricted games. Update your profile first.");
        }

        int age = calculateAge(userProfile.getDateOfBirth(), referenceDate == null ? LocalDate.now(ZoneOffset.UTC) : referenceDate);
        Integer minAge = sport.getMinAge();
        Integer maxAge = sport.getMaxAge();

        if (minAge != null && age < minAge) {
            throw new BadRequestException(sport.getName() + " is only available for players aged " + describeAgeRange(minAge, maxAge) + ". Your current age is " + age + ".");
        }
        if (maxAge != null && age > maxAge) {
            throw new BadRequestException(sport.getName() + " is only available for players aged " + describeAgeRange(minAge, maxAge) + ". Your current age is " + age + ".");
        }
    }

    public String describeAgeRange(Integer minAge, Integer maxAge) {
        if (minAge != null && maxAge != null) {
            return minAge + " to " + maxAge;
        }
        if (minAge != null) {
            return minAge + " and above";
        }
        if (maxAge != null) {
            return maxAge + " and below";
        }
        return "any age";
    }
}