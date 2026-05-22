package pl.tactics.domain;

import java.util.List;
import java.util.Objects;

public record CampaignState(
    String campaignId,
    String scenarioId,
    int turnNumber,
    Side activeSide,
    List<Unit> units,
    List<Order> pendingOrders
) {
    public CampaignState {
        Objects.requireNonNull(campaignId, "campaignId must not be null");
        Objects.requireNonNull(scenarioId, "scenarioId must not be null");
        Objects.requireNonNull(activeSide, "activeSide must not be null");
        units = List.copyOf(Objects.requireNonNull(units, "units must not be null"));
        pendingOrders = List.copyOf(Objects.requireNonNull(pendingOrders, "pendingOrders must not be null"));
    }
}
