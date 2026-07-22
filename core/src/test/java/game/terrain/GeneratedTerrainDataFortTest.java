package game.terrain;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedTerrainDataFortTest {
    private static final int FORT_AREA_PIXELS = 112;
    private static final int FORT_BBOX_SIZE_PIXELS = 16;
    private static final int FORT_TILES_PER_EDGE = 2;

    @Test
    void allFortMarkers_areDetectedAsAlignedFortComponents() {
        var blackPixels = buildBlackPixelMap();
        var fortComponents = findFortComponents(blackPixels);

        assertThat(fortComponents).hasSize(4);

        assertComponentExists(fortComponents, 496, 152, 511, 167, 112);
        assertComponentExists(fortComponents, 352, 184, 367, 199, 112);
        assertComponentExists(fortComponents, 872, 216, 887, 231, 112);
        assertComponentExists(fortComponents, 1400, 232, 1415, 247, 112);

        for (var component : fortComponents) {
            assertThat(component.minX() % GeneratedTerrainData.SOURCE_TILE_SIZE).isZero();
            assertThat(component.minY() % GeneratedTerrainData.SOURCE_TILE_SIZE).isZero();
        }
    }

    @Test
    void allFortMarkers_useSingleCanonicalPattern() {
        var blackPixels = buildBlackPixelMap();
        var fortComponents = findFortComponents(blackPixels);

        assertThat(fortComponents).hasSize(4);

        var canonicalMask = componentMask(blackPixels, fortComponents.get(0));
        var fortMasks = fortComponents.stream()
            .map(component -> componentMask(blackPixels, component))
            .toList();

        assertThat(fortMasks).allMatch(mask -> Arrays.deepEquals(mask, canonicalMask));
    }

    @Test
    void dominantTerrain_marksEveryDetectedFortTileAreaAsFort() {
        var blackPixels = buildBlackPixelMap();
        var fortComponents = findFortComponents(blackPixels);

        assertThat(fortComponents).hasSize(4);

        for (var component : fortComponents) {
            var tileX = component.minX() / GeneratedTerrainData.SOURCE_TILE_SIZE;
            var tileYTop = component.minY() / GeneratedTerrainData.SOURCE_TILE_SIZE;

            for (int rowTop = tileYTop; rowTop < tileYTop + FORT_TILES_PER_EDGE; rowTop++) {
                for (int col = tileX; col < tileX + FORT_TILES_PER_EDGE; col++) {
                    assertThat(terrainCodeAt(col, rowTop)).isEqualTo(GeneratedTerrainData.TERRAIN_FORT);
                }
            }
        }
    }

    private static void assertComponentExists(List<PixelComponent> components,
                                              int minX,
                                              int minY,
                                              int maxX,
                                              int maxY,
                                              int area) {
        var found = components.stream().anyMatch(component ->
            component.minX() == minX
                && component.minY() == minY
                && component.maxX() == maxX
                && component.maxY() == maxY
                && component.area() == area
        );

        assertThat(found).isTrue();
    }

    private static boolean[][] componentMask(boolean[][] blackPixels, PixelComponent component) {
        var mask = new boolean[FORT_BBOX_SIZE_PIXELS][FORT_BBOX_SIZE_PIXELS];
        for (int y = 0; y < FORT_BBOX_SIZE_PIXELS; y++) {
            for (int x = 0; x < FORT_BBOX_SIZE_PIXELS; x++) {
                mask[y][x] = blackPixels[component.minY() + y][component.minX() + x];
            }
        }
        return mask;
    }

    private static int terrainCodeAt(int tileX, int rowTop) {
        var index = rowTop * GeneratedTerrainData.MAP_WIDTH_TILES + tileX;
        return GeneratedTerrainData.TILE_DOMINANT_TERRAIN[index] & 0xFF;
    }

    private static boolean[][] buildBlackPixelMap() {
        var widthPixels = GeneratedTerrainData.MAP_WIDTH_TILES * GeneratedTerrainData.SOURCE_TILE_SIZE;
        var heightPixels = GeneratedTerrainData.MAP_HEIGHT_TILES * GeneratedTerrainData.SOURCE_TILE_SIZE;
        var black = new boolean[heightPixels][widthPixels];

        for (int rowTop = 0; rowTop < GeneratedTerrainData.MAP_HEIGHT_TILES; rowTop++) {
            for (int col = 0; col < GeneratedTerrainData.MAP_WIDTH_TILES; col++) {
                var tileId = GeneratedTerrainData.MAP_TILE_IDS[rowTop * GeneratedTerrainData.MAP_WIDTH_TILES + col] & 0xFFFF;
                var pattern = GeneratedTerrainData.UNIQUE_TILE_PATTERNS[tileId];
                for (int py = 0; py < GeneratedTerrainData.SOURCE_TILE_SIZE; py++) {
                    for (int px = 0; px < GeneratedTerrainData.SOURCE_TILE_SIZE; px++) {
                        black[rowTop * GeneratedTerrainData.SOURCE_TILE_SIZE + py][col * GeneratedTerrainData.SOURCE_TILE_SIZE + px] =
                            (pattern[py * GeneratedTerrainData.SOURCE_TILE_SIZE + px] & 0xFF) == GeneratedTerrainData.TERRAIN_VOID;
                    }
                }
            }
        }

        return black;
    }

    private static List<PixelComponent> findFortComponents(boolean[][] blackPixels) {
        return allBlackPixelComponents(blackPixels).stream()
            .filter(component -> component.width() == FORT_BBOX_SIZE_PIXELS)
            .filter(component -> component.height() == FORT_BBOX_SIZE_PIXELS)
            .filter(component -> component.area() == FORT_AREA_PIXELS)
            .sorted(Comparator.comparingInt(PixelComponent::minY).thenComparingInt(PixelComponent::minX))
            .toList();
    }

    private static List<PixelComponent> allBlackPixelComponents(boolean[][] blackPixels) {
        var height = blackPixels.length;
        var width = blackPixels[0].length;
        var visited = new boolean[height][width];
        var queue = new ArrayDeque<int[]>();
        var components = new ArrayList<PixelComponent>();

        int[] dx = {1, -1, 0, 0, 1, 1, -1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, 1, -1};

        for (int startY = 0; startY < height; startY++) {
            for (int startX = 0; startX < width; startX++) {
                if (visited[startY][startX]) {
                    continue;
                }

                visited[startY][startX] = true;
                if (!blackPixels[startY][startX]) {
                    continue;
                }

                queue.add(new int[] {startX, startY});

                var minX = startX;
                var maxX = startX;
                var minY = startY;
                var maxY = startY;
                var area = 0;

                while (!queue.isEmpty()) {
                    var point = queue.removeFirst();
                    var x = point[0];
                    var y = point[1];
                    if (!blackPixels[y][x]) {
                        continue;
                    }

                    area++;
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);

                    for (int i = 0; i < dx.length; i++) {
                        var nx = x + dx[i];
                        var ny = y + dy[i];
                        if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                            continue;
                        }
                        if (visited[ny][nx]) {
                            continue;
                        }
                        visited[ny][nx] = true;
                        queue.addLast(new int[] {nx, ny});
                    }
                }

                components.add(new PixelComponent(minX, minY, maxX, maxY, area));
            }
        }

        return components;
    }

    private record PixelComponent(int minX, int minY, int maxX, int maxY, int area) {
        private int width() {
            return maxX - minX + 1;
        }

        private int height() {
            return maxY - minY + 1;
        }
    }
}
