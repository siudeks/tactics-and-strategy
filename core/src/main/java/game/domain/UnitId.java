package game.domain;

public record UnitId(String value) {
    @Override
    public String toString() {
        return value;
    }
}
