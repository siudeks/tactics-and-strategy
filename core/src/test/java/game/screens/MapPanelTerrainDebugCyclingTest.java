package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapPanelTerrainDebugCyclingTest {

    @Test
    void cycleTerrainDebugLayer_advancesFromAllToVoid() {
        var nextLayer = MapPanel.cycleTerrainDebugLayer(MapPanel.TerrainDebugLayer.ALL);

        assertEquals(MapPanel.TerrainDebugLayer.VOID, nextLayer);
    }

    @Test
    void cycleTerrainDebugLayer_wrapsFromWaterBackToAll() {
        var nextLayer = MapPanel.cycleTerrainDebugLayer(MapPanel.TerrainDebugLayer.WATER);

        assertEquals(MapPanel.TerrainDebugLayer.ALL, nextLayer);
    }

    @Test
    void cycleTerrainDebugLayer_advancesThroughEachTerrainLayer() {
        var nextLayer = MapPanel.cycleTerrainDebugLayer(MapPanel.TerrainDebugLayer.VOID);

        assertEquals(MapPanel.TerrainDebugLayer.SAND, nextLayer);
    }

    @Test
    void cycleTerrainDebugLayer_advancesFromMountainToFort() {
        var nextLayer = MapPanel.cycleTerrainDebugLayer(MapPanel.TerrainDebugLayer.MOUNTAIN);

        assertEquals(MapPanel.TerrainDebugLayer.FORT, nextLayer);
    }

    @Test
    void currentTerrainDebugLayerName_returnsHumanReadableLabel() {
        var layerName = MapPanel.TerrainDebugLayer.MOUNTAIN.displayName();

        assertEquals("Mountain", layerName);
    }
}
