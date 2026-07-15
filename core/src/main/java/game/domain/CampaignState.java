package game.domain;

import java.util.List;

public record CampaignState(
    String campaignId,
    String scenarioId,
    int turnNumber,
    Side activeSide,
    List<Unit> units,
    List<Order> pendingOrders
) {
    public CampaignState {
        units = List.copyOf(units);
        pendingOrders = List.copyOf(pendingOrders);
    }
}
