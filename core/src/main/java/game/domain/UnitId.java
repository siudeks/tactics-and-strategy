package game.domain;

public record UnitId(String value) {
    public static UnitId of(String value) {
        return new UnitId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
