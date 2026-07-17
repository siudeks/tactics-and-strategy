package game.platform;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import game.terrain.GeneratedTerrainData;
import game.terrain.TerrainMapDefinition;

public final class TerrainTileAtlas implements Disposable {
    private static final int SOURCE_TILE_SIZE = GeneratedTerrainData.SOURCE_TILE_SIZE;

    private Texture atlasTexture;
    private TextureRegion[] regions;
    private int[] regionX;
    private int[] regionY;
    private int tileCount;
    private int columns;

    public TerrainTileAtlas(TerrainMapDefinition mapDefinition) {
        tileCount = mapDefinition.getUniqueTileCount();
        columns = (int) Math.ceil(Math.sqrt(tileCount));

        regionX = new int[tileCount];
        regionY = new int[tileCount];
        regions = new TextureRegion[tileCount];
        for (int tileId = 0; tileId < tileCount; tileId++) {
            regionX[tileId] = (tileId % columns) * SOURCE_TILE_SIZE;
            regionY[tileId] = (tileId / columns) * SOURCE_TILE_SIZE;
        }

        rebuild(mapDefinition);
    }

    public void rebuild(TerrainMapDefinition mapDefinition) {
        if (atlasTexture != null) {
            atlasTexture.dispose();
        }

        var rows = (tileCount + columns - 1) / columns;

        var atlas = new Pixmap(columns * SOURCE_TILE_SIZE, rows * SOURCE_TILE_SIZE, Pixmap.Format.RGBA8888);
        atlas.setColor(0f, 0f, 0f, 0f);
        atlas.fill();

        for (int tileId = 0; tileId < tileCount; tileId++) {
            var pattern = mapDefinition.getTilePattern(tileId);
            var atlasX = regionX[tileId];
            var atlasY = regionY[tileId];

            for (int y = 0; y < SOURCE_TILE_SIZE; y++) {
                for (int x = 0; x < SOURCE_TILE_SIZE; x++) {
                    var terrainCode = pattern[y * SOURCE_TILE_SIZE + x] & 0xFF;
                    if (terrainCode == GeneratedTerrainData.TERRAIN_VOID) {
                        atlas.drawPixel(atlasX + x, atlasY + y, 0x00000000);
                        continue;
                    }

                    // Pixmap RGBA8888 expects RGBA-packed int, not ABGR.
                    var terrainColor = mapDefinition.getTerrainColor(terrainCode);
                    var packedColor = com.badlogic.gdx.graphics.Color.rgba8888(
                        terrainColor.r(),
                        terrainColor.g(),
                        terrainColor.b(),
                        terrainColor.a()
                    );
                    atlas.drawPixel(atlasX + x, atlasY + y, packedColor);
                }
            }
        }

        atlasTexture = new Texture(atlas);
        atlas.dispose();

        for (int tileId = 0; tileId < tileCount; tileId++) {
            regions[tileId] = new TextureRegion(atlasTexture, regionX[tileId], regionY[tileId], SOURCE_TILE_SIZE, SOURCE_TILE_SIZE);
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
