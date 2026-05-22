# T04 Rendering Coordinate Spec

## Purpose

Define the deterministic conversion from scenario unit tile coordinates to BattlefieldScreen map-panel draw coordinates for 2x2 tile unit icons.

## Constants

- `DRAW_TILE_SIZE = 16f`
- `UNIT_SIZE_IN_TILES = 2f`
- `UNIT_DRAW_SIZE = 32f`

## Coordinate Spaces

### Tile Space

- Unit data uses integer `tileX` and `tileY`.
- `tileX` increases left to right.
- `tileY` is a top-origin row index, matching `TerrainMapDefinition.getMapTileId(column, rowTop)` and the existing `drawTerrain()` loop.
- `tileY = 0` is the top map row.

### World Space

- World origin is bottom-left of the whole map.
- Terrain tiles are already rendered in world space with:

```text
tileWorldX = tileX * DRAW_TILE_SIZE
tileWorldY = (mapHeightTiles - tileY - 1) * DRAW_TILE_SIZE
```

- A unit icon is 2 tiles wide by 2 tiles tall and `drawUnitIcon()` expects the icon's bottom-left draw position.
- The unit coordinate is the top-left tile of that 2x2 footprint.

## Tile -> World Conversion

Use the unit tile as the top-left anchor of the icon footprint.

```text
iconWorldX = tileX * DRAW_TILE_SIZE
iconWorldY = (mapHeightTiles - tileY - UNIT_SIZE_IN_TILES) * DRAW_TILE_SIZE
iconWorldWidth = UNIT_SIZE_IN_TILES * DRAW_TILE_SIZE
iconWorldHeight = UNIT_SIZE_IN_TILES * DRAW_TILE_SIZE
```

Equivalent with current constants:

```text
iconWorldX = tileX * 16
iconWorldY = (mapHeightTiles - tileY - 2) * 16
iconWorldWidth = 32
iconWorldHeight = 32
```

## World -> Screen Conversion

MapPanel screen coordinates are panel-relative world coordinates after camera subtraction.

```text
iconScreenX = panelX + iconWorldX - cameraX
iconScreenY = panelY + iconWorldY - cameraY
```

Application order is fixed:

1. Convert tile coordinates to world coordinates.
2. Subtract `cameraX` and `cameraY`.
3. Add `panelX` and `panelY`.
4. Draw the icon at the resulting bottom-left screen position.

Do not subtract the camera before applying the Y-axis flip. The flip is part of tile-to-world conversion, not screen conversion.

## Y-Axis Orientation Rules

- Scenario and terrain row indexing are top-origin.
- libGDX drawing coordinates are bottom-origin.
- Therefore Y must be flipped exactly once when entering world space.
- There is no additional flip inside `drawUnitIcon()` because its internal 16x16 pattern already draws upward from the supplied bottom-left anchor.

## Camera Rules

- `cameraX` and `cameraY` represent the world-space offset of the visible panel origin.
- Positive `cameraX` moves the view to the right through the map, so rendered icons move left on screen.
- Positive `cameraY` moves the view upward through the map, so rendered icons move down on screen.
- Camera clamping remains:

```text
0 <= cameraX <= max(0, mapWorldWidth - panelWidth)
0 <= cameraY <= max(0, mapWorldHeight - panelHeight)
```

## Clipping and Visibility

Use icon-bounds intersection against the visible panel rectangle. Skip draw only when the icon is fully outside the panel.

```text
visibleLeft = panelX
visibleRight = panelX + panelWidth
visibleBottom = panelY
visibleTop = panelY + panelHeight

iconLeft = iconScreenX
iconRight = iconScreenX + UNIT_DRAW_SIZE
iconBottom = iconScreenY
iconTop = iconScreenY + UNIT_DRAW_SIZE
```

Cull when any of the following is true:

```text
iconRight <= visibleLeft
iconLeft >= visibleRight
iconTop <= visibleBottom
iconBottom >= visibleTop
```

This is a visibility cull, not geometric clipping. Partially visible icons still draw.

## Worked Examples

Assume `mapWidthTiles = 10`, `mapHeightTiles = 10`, `DRAW_TILE_SIZE = 16`, `UNIT_DRAW_SIZE = 32`.

### Example A: Top-left usable edge anchor

- Input tile: `(0, 0)`
- World:

```text
iconWorldX = 0 * 16 = 0
iconWorldY = (10 - 0 - 2) * 16 = 128
```

- With `panelX = 40`, `panelY = 24`, `cameraX = 0`, `cameraY = 0`:

```text
iconScreenX = 40
iconScreenY = 152
```

- Result: icon occupies the top-left 2x2 footprint on the map.

### Example B: Bottom-right usable edge anchor

- Input tile: `(8, 8)`
- World:

```text
iconWorldX = 8 * 16 = 128
iconWorldY = (10 - 8 - 2) * 16 = 0
```

- With `panelX = 40`, `panelY = 24`, `cameraX = 0`, `cameraY = 0`:

```text
iconScreenX = 168
iconScreenY = 24
```

- Result: icon footprint touches the map's bottom and right edges without overflowing.

### Example C: Center tile with camera offset

- Input tile: `(4, 4)`
- World:

```text
iconWorldX = 4 * 16 = 64
iconWorldY = (10 - 4 - 2) * 16 = 64
```

- With `panelX = 100`, `panelY = 50`, `cameraX = 32`, `cameraY = 16`:

```text
iconScreenX = 100 + 64 - 32 = 132
iconScreenY = 50 + 64 - 16 = 98
```

- Result: same world position, shifted by the camera after conversion.

### Example D: Bootstrap scenario unit

- Input tile: `(1, 1)` from `desert-rats-bootstrap`
- World:

```text
iconWorldX = 16
iconWorldY = (10 - 1 - 2) * 16 = 112
```

- With zero camera and panel origin `(0, 0)`:

```text
iconScreenX = 16
iconScreenY = 112
```

## Testable Invariants

- Increasing `tileX` by 1 increases `iconWorldX` by 16.
- Increasing `tileY` by 1 decreases `iconWorldY` by 16.
- With zero camera, two units in the same row differ only by X.
- With identical tile coordinates, increasing `cameraX` by 16 decreases `iconScreenX` by 16.
- With identical tile coordinates, increasing `cameraY` by 16 decreases `iconScreenY` by 16.
- For a full 2x2 footprint inside the map, the largest safe anchor is `(mapWidthTiles - 2, mapHeightTiles - 2)`.

## Implementation Note For T05

`drawUnit()` should be replaced by iteration over runtime campaign units and should compute icon placement with this conversion contract before calling `drawUnitIcon(batch, iconScreenX, iconScreenY, UNIT_DRAW_SIZE)`.