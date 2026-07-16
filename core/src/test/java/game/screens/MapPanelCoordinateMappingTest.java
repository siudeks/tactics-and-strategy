package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapPanelCoordinateMappingTest {

    @Test
    void scenarioTileFromTerrainTile_returnsNull_whenTerrainTileIsNull() {
        var result = MapPanel.scenarioTileFromTerrainTile(
            null,
            201,
            59,
            10
        );

        assertNull(result);
    }

    @Test
    void scenarioTileFromTerrainTile_keepsRightSideOfVisibleMapClickable() {
        var result = MapPanel.scenarioTileFromTerrainTile(
            new BattlefieldScreen.TileCoord(180, 52),
            201,
            59,
            10
        );

        assertNotNull(result);
        assertEquals(new BattlefieldScreen.TileCoord(180, 3), result);
    }

    @Test
    void scenarioTileFromTerrainTile_acceptsRowsAboveScenarioBand() {
        var result = MapPanel.scenarioTileFromTerrainTile(
            new BattlefieldScreen.TileCoord(50, 20),
            201,
            59,
            10
        );

        assertNotNull(result);
        // 20 - (59 - 10) = -29
        assertEquals(new BattlefieldScreen.TileCoord(50, -29), result);
    }

    @Test
    void scenarioTileFromTerrainTile_returnsNull_whenXOutsideTerrainWidth() {
        var result = MapPanel.scenarioTileFromTerrainTile(
            new BattlefieldScreen.TileCoord(250, 40),
            201,
            59,
            10
        );

        assertNull(result);
    }
}
