package game.domain;

public record ScenarioId(String value) {
    @Override
    public String toString() {
        return value;
    }
}
