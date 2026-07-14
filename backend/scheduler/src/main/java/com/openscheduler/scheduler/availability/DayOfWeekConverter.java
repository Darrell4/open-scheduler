package com.openscheduler.scheduler.availability;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;

/**
 * Maps {@link DayOfWeek} to its ISO-8601 numeric value (1 = Monday ... 7 = Sunday).
 * JPA's default ordinal mapping would be 0-based, which would not match the schema.
 */
@Converter(autoApply = true)
public class DayOfWeekConverter implements AttributeConverter<DayOfWeek, Short> {

    @Override
    public Short convertToDatabaseColumn(DayOfWeek attribute) {
        return attribute == null ? null : (short) attribute.getValue();
    }

    @Override
    public DayOfWeek convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : DayOfWeek.of(dbData);
    }
}
