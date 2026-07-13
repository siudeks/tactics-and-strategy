package game.domain;

public record CampaignId(String value) {

    @Override
    public String toString() {
        return value;
    }
}
