package game.terrain;

import com.badlogic.gdx.graphics.Color;

public final class TerrainMapDefinition {
    private static final int TILE_PATTERN_PIXELS = GeneratedTerrainData.SOURCE_TILE_SIZE * GeneratedTerrainData.SOURCE_TILE_SIZE;

    private final int widthTiles;
    private final int heightTiles;
    private final short[] mapTileIds;
    private final byte[][] uniqueTilePatterns;
    private final byte[] tileDominantTerrain;
    private Color[] terrainColors;

    public TerrainMapDefinition() {
        this.widthTiles = GeneratedTerrainData.MAP_WIDTH_TILES;
        this.heightTiles = GeneratedTerrainData.MAP_HEIGHT_TILES;
        this.mapTileIds = GeneratedTerrainData.MAP_TILE_IDS;
        this.uniqueTilePatterns = GeneratedTerrainData.UNIQUE_TILE_PATTERNS;
        this.tileDominantTerrain = GeneratedTerrainData.TILE_DOMINANT_TERRAIN;
        this.terrainColors = buildTerrainColors(GeneratedTerrainData.terrainColorsRgbDefault());

        validate();
    }

    public int getWidthTiles() {
        return widthTiles;
    }

    public int getHeightTiles() {
        return heightTiles;
    }

    public int getMapTileId(int column, int rowTop) {
        return mapTileIds[rowTop * widthTiles + column] & 0xFFFF;
    }

    public byte[] getTilePattern(int tileId) {
        return uniqueTilePatterns[tileId];
    }

    public int getTerrainCode(int mapIndex) {
        return tileDominantTerrain[mapIndex] & 0xFF;
    }

    public Color getTerrainColor(int terrainCode) {
        return terrainColors[terrainCode];
    }

    public int getUniqueTileCount() {
        return uniqueTilePatterns.length;
    }

    private Color[] buildTerrainColors(int[][] rgb) {
        Color[] colors = new Color[rgb.length];
        for (int i = 0; i < rgb.length; i++) {
            colors[i] = new Color(rgb[i][0] / 255f, rgb[i][1] / 255f, rgb[i][2] / 255f, 1f);
        }
        return colors;
    }

    private void validate() {
        if (mapTileIds.length != widthTiles * heightTiles) {
            throw new IllegalStateException("Map tile ids size mismatch");
        }

        if (tileDominantTerrain.length != widthTiles * heightTiles) {
            throw new IllegalStateException("Dominant terrain size mismatch");
        }

        for (byte[] pattern : uniqueTilePatterns) {
            if (pattern.length != TILE_PATTERN_PIXELS) {
                throw new IllegalStateException("Invalid tile pattern size: " + pattern.length);
            }
        }

        for (short mapTileId : mapTileIds) {
            int tileId = mapTileId & 0xFFFF;
            if (tileId < 0 || tileId >= uniqueTilePatterns.length) {
                throw new IllegalStateException("Map references unknown tile id: " + tileId);
            }
        }
    }
}
