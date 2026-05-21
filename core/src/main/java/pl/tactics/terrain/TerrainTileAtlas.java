package pl.tactics.terrain;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public final class TerrainTileAtlas implements Disposable {
    private static final int SOURCE_TILE_SIZE = GeneratedTerrainData.SOURCE_TILE_SIZE;

    private final Texture atlasTexture;
    private final TextureRegion[] regions;

    public TerrainTileAtlas(TerrainMapDefinition mapDefinition) {
        int tileCount = mapDefinition.getUniqueTileCount();
        int columns = (int) Math.ceil(Math.sqrt(tileCount));
        int rows = (tileCount + columns - 1) / columns;

        Pixmap atlas = new Pixmap(columns * SOURCE_TILE_SIZE, rows * SOURCE_TILE_SIZE, Pixmap.Format.RGBA8888);
        atlas.setColor(0f, 0f, 0f, 0f);
        atlas.fill();

        for (int tileId = 0; tileId < tileCount; tileId++) {
            byte[] pattern = mapDefinition.getTilePattern(tileId);
            int atlasX = (tileId % columns) * SOURCE_TILE_SIZE;
            int atlasY = (tileId / columns) * SOURCE_TILE_SIZE;

            for (int y = 0; y < SOURCE_TILE_SIZE; y++) {
                for (int x = 0; x < SOURCE_TILE_SIZE; x++) {
                    int terrainCode = pattern[y * SOURCE_TILE_SIZE + x] & 0xFF;
                    if (terrainCode == GeneratedTerrainData.TERRAIN_VOID) {
                        atlas.drawPixel(atlasX + x, atlasY + y, 0x00000000);
                        continue;
                    }

                    // Pixmap RGBA8888 expects RGBA-packed int, not ABGR.
                    int packedColor = com.badlogic.gdx.graphics.Color.rgba8888(mapDefinition.getTerrainColor(terrainCode));
                    atlas.drawPixel(atlasX + x, atlasY + y, packedColor);
                }
            }
        }

        atlasTexture = new Texture(atlas);
        atlas.dispose();

        regions = new TextureRegion[tileCount];
        for (int tileId = 0; tileId < tileCount; tileId++) {
            int atlasX = (tileId % columns) * SOURCE_TILE_SIZE;
            int atlasY = (tileId / columns) * SOURCE_TILE_SIZE;
            regions[tileId] = new TextureRegion(atlasTexture, atlasX, atlasY, SOURCE_TILE_SIZE, SOURCE_TILE_SIZE);
        }
    }

    public TextureRegion getRegion(int tileId) {
        return regions[tileId];
    }

    @Override
    public void dispose() {
        atlasTexture.dispose();
    }
}
