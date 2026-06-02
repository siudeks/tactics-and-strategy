package game.terrain;

import com.badlogic.gdx.graphics.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class TerrainMapDefinitionTest {

    @Test
    void constructor_usesImprovedPaletteForSandColor() {
        TerrainMapDefinition definition = new TerrainMapDefinition();

        Color sand = definition.getTerrainColor(GeneratedTerrainData.TERRAIN_SAND);

        assertEquals(194f / 255f, sand.r, 0.0001f);
        assertEquals(171f / 255f, sand.g, 0.0001f);
        assertEquals(109f / 255f, sand.b, 0.0001f);
    }

    @Test
    void terrainColorsRgbDefault_returnsCopyOfImprovedPalette() {
        int[][] defaults = GeneratedTerrainData.terrainColorsRgbDefault();
        int[][] improved = GeneratedTerrainData.terrainColorsRgbImproved();

        assertNotSame(improved, defaults);
        assertNotSame(improved[0], defaults[0]);
        assertEquals(improved[GeneratedTerrainData.TERRAIN_WATER][0], defaults[GeneratedTerrainData.TERRAIN_WATER][0]);
        assertEquals(improved[GeneratedTerrainData.TERRAIN_WATER][1], defaults[GeneratedTerrainData.TERRAIN_WATER][1]);
        assertEquals(improved[GeneratedTerrainData.TERRAIN_WATER][2], defaults[GeneratedTerrainData.TERRAIN_WATER][2]);

        defaults[GeneratedTerrainData.TERRAIN_WATER][0] = 0;

        int[][] improvedAfterMutation = GeneratedTerrainData.terrainColorsRgbImproved();
        assertEquals(56, improvedAfterMutation[GeneratedTerrainData.TERRAIN_WATER][0]);
    }
}
