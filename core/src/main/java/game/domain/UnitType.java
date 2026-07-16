package game.domain;

import java.util.Locale;

public enum UnitType {
    MEDIUM_TANK,
    LIGHT_TANK,
    INFANTRY_TANK,
    RECCE,
    MOTORISED_INFANTRY,
    FOOT_INFANTRY,
    SUPPORT_GROUP,
    ANTI_TANK,
    ARTILLERY,
    HQ;

    public static UnitType fromScenarioValue(String value) {
        var normalized = value.trim()
            .toUpperCase(Locale.ROOT)
            .replace(' ', '_')
            .replace('-', '_');

        return UnitType.valueOf(normalized);
    }
}
