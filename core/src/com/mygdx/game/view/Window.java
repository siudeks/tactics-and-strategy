package com.mygdx.game.view;

import java.util.Set;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.domain.CityEntity;
import com.mygdx.game.domain.GeoPoint;
import com.mygdx.game.domain.IntendedMapCentre;
import com.mygdx.game.domain.IslandEntity;
import com.mygdx.game.domain.IslandEntityExtensions;
import com.mygdx.game.domain.IslandEntityGenerator;
import com.mygdx.game.domain.LandUnitEntity;
import com.mygdx.game.extensions.SpriteBatchUtils;
import com.mygdx.game.resources.SelectionTexture;
import com.mygdx.game.resources.TextureItem;
import com.mygdx.game.resources.WaterTextures;
import com.mygdx.game.runtime.IBatchDrawer;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

/**
 * Contains all entities which need to be visible in Game window and converts them
 * to textures.
 */
@Singleton
public final class Window {
    private Map<GeoPoint, LocationType> mapPoints = HashMap.<GeoPoint, LocationType>empty();

    private WaterTextures water;
    private List<ITileStrategy> mergeStrategies = List.empty();
    private List<ITileStrategy> strategies;

    @Inject
    private Set<IBatchDrawer> batchDrawers;

    @Inject
    public Window(WaterTextures water, Set<ITileStrategy> strategies) {
        this.water = water;

        mergeStrategies = mergeStrategies
            .append(new Water(water))
            .append(new Ground(water))
            .append(new CoastWithLandToTheEast(water))
            .append(new CoastWithLandToTheNorth(water))
            .append(new CoastWithLandToTheWest(water))
            .append(new CoastWithLandToTheNorthEast(water))
            .append(new CoastWithLandToTheSouthWest(water))
            .append(new CoastWithLandToTheSouthStrategy(water))
            .append(new WaterWithLandSouthEast(water))
            .append(new WaterWithLandNorthEast(water))
            .append(new WaterWithLandNorthSouthEast(water))
            ;

        this.strategies = List.ofAll(strategies);
    }

    public void addIsland(IslandEntity island) {
        for (var item : IslandEntityExtensions.generatePoints(island)) {
            var point = new GeoPoint(item.x, item.y);
            mapPoints = mapPoints.put(point, LocationType.Ground);
        }
    }

    /**
     * Needs to be invoked later then <see cref="AddIsland(IslandEntity)"/>.
     * @param entity
     */
    public void addCity(CityEntity entity)
    {
        var point = new GeoPoint(entity.getX(), entity.getY());
        mapPoints.put(point, LocationType.City);
    }

    /**
     * 
     * @param entity
     */
    public void include(LandUnitEntity entity)
    {
        var point = new GeoPoint(entity.getX(), entity.getY());
        mapPoints.put(point, LocationType.LandUnit);
    }

    /**
     * Generates list of textures for given square, when left bottom corner is provided.
     * @return Sequence contains textures, starting from left bottom corner and returning rows first
     */
    public Seq<PointContext> getWindow(int lbcx, int lbcy, int width, int height) {
        var result = List.<PointContext>empty();
        for (int dy = 0; dy < height; dy++) 
            for (int dx = 0; dx < width; dx++) {
                var geox = lbcx + dx;
                var geoy = lbcy + dy;

                var area = new LocationType[9];
                var centerOfArea = new GeoPoint(geox, geoy);
                var relativeCenterOfArea = new GeoPoint(dx, dy);

                for (int i = 0; i < 9; i++) area[i] = LocationType.Water;

                area[Directions.NeighborTopLeft] = mapPoints.get(centerOfArea.topLeft()).getOrElse(LocationType.Water);
                area[Directions.NeighborNorth] = mapPoints.get(centerOfArea.top()).getOrElse(LocationType.Water);
                area[Directions.NeighborTopRight] = mapPoints.get(centerOfArea.topRight()).getOrElse(LocationType.Water);
                area[Directions.NeighborWest] = mapPoints.get(centerOfArea.left()).getOrElse(LocationType.Water);
                area[Directions.NeighborThis] = mapPoints.get(centerOfArea).getOrElse(LocationType.Water);
                area[Directions.NeighborEast] = mapPoints.get(centerOfArea.right()).getOrElse(LocationType.Water);
                area[Directions.NeighborDownLeft] = mapPoints.get(centerOfArea.downLeft()).getOrElse(LocationType.Water);
                area[Directions.NeighborSouth] = mapPoints.get(centerOfArea.down()).getOrElse(LocationType.Water);
                area[Directions.NeighborDownRight] = mapPoints.get(centerOfArea.downRight()).getOrElse(LocationType.Water);


                var allStrategies = strategies.appendAll(mergeStrategies);
                var handler = allStrategies.filter(it -> it.canExecute(area)).headOption();
                var centerTexture = handler.map(it -> it.execute(area)).getOrElse(water.getBlank());

                result = result.append(new PointContext(relativeCenterOfArea, centerTexture));
            }
        return result;
    }

    public void initialize()
    {
        // temporar variables to keep sample textures for demo purposes.
        var island = IslandEntityGenerator.random(new GeoPoint(5, 5));

        this.addIsland(island);
        // var island1 = new IslandEntity(new GeoPoint[] {new GeoPoint(1, 1), new GeoPoint(2, 2)});
        // this.addIsland(island1);
        this.addCity(new CityEntity(10, 10));
        this.include(new LandUnitEntity(11, 10));
    }

    Texture terrainTexture;
    Texture desertRatsTextures;

    public void onLoaded(Texture texture, TextureItem item) {
        if (item == TextureItem.TERRAIN) terrainTexture = texture;
        if (item == TextureItem.DESERT_RATES) desertRatsTextures = texture;
    }

    @Inject
    IntendedMapCentre intendedMapCentre;

    public void onDraw(SpriteBatch spriteBatch) {

        var x = intendedMapCentre.getX();
        var y = intendedMapCentre.getY();

        // display sample island
        var points = this.getWindow(0 + x, 0 + y, 20, 20);

        for (var it : points) {
            var position = new Vector2(
                it.geoPoint.x * Config.SpriteSize,
                it.geoPoint.y * Config.SpriteSize);
            if (it.texture == null) {
                assert false;
            }
            SpriteBatchUtils.draw(spriteBatch, position, it.texture);
        }

        for (var drawer : this.batchDrawers) {
            drawer.OnDraw(spriteBatch);
        }
    }

    @Inject
    SelectionTexture st;


    /**
     * Holds together geopoint and texture used for that point to draw it.
     */
    public class PointContext {
        public final GeoPoint geoPoint;
        public final TextureHolder texture;

        public PointContext(GeoPoint geopoint, TextureHolder texture) {
            this.geoPoint = geopoint;
            this.texture = texture;
        }
    }
}

// defines single texture generator strategy
// area: small array 3*3, where central point defines the point which need th have
// generated texture, and the rest of array items defines neighbers with indexes as constatns above.
// if strategy can't generate texture, returns defaultTexture.
interface MergeStrategy extends ITileStrategy {
}

final class CoastWithLandToTheEast implements MergeStrategy {
    private WaterTextures water;
    CoastWithLandToTheEast(WaterTextures water) {
        this.water = water;
    }

    @Override
    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;
        return true;
    }

    @Override
    public TextureHolder execute(LocationType[] neighbors) {
        return water.getLandEast();
    }

}

final class CoastWithLandToTheNorth implements MergeStrategy {

    private WaterTextures water;

    CoastWithLandToTheNorth(WaterTextures water) {
        this.water = water;
    }

    @Override
    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Ground) return false;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;
        return true;
    }

    @Override
    public TextureHolder execute(LocationType[] neighbors) {
        return water.getLandNorth();
    }

}

final class CoastWithLandToTheWest implements MergeStrategy {

    private WaterTextures water;

    CoastWithLandToTheWest(WaterTextures water) {
        this.water = water;
    }

    @Override
    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;
        return true;
    }

    @Override
    public TextureHolder execute(LocationType[] neighbors) {
        return water.getLandWest();
    }

}

final class CoastWithLandToTheNorthEast implements MergeStrategy {

    private WaterTextures water;

    CoastWithLandToTheNorthEast(WaterTextures water) {
        this.water = water;
    }
    
    @Override
    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;
        return true;
    }

    @Override
    public TextureHolder execute(LocationType[] neighbors) {
        return water.getLandNorthEast();
    }
}

final class CoastWithLandToTheSouthWest implements MergeStrategy {

    private WaterTextures water;

    CoastWithLandToTheSouthWest(WaterTextures water) {
        this.water = water;
    }
    
    @Override
    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;
    
        return true;
    }

    @Override
    public TextureHolder execute(LocationType[] neighbors) {
        return water.getLandSouthWest();
    }
}

final class CoastWithLandToTheSouthStrategy implements ITileStrategy {

    private final WaterTextures textures;

    @Inject
    public CoastWithLandToTheSouthStrategy(WaterTextures waterTextures) {
        this.textures = waterTextures;
    }

    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;

        return true;

    }

    public TextureHolder execute(LocationType[] neighbors) {
        return textures.getLandSouth();
    }
}

final class WaterWithLandSouthEast extends BaseGroundStrategy {

    @Inject
    public WaterWithLandSouthEast(WaterTextures waterTextures) {
        super(LocationType.Water, LocationType.Ground, LocationType.Ground, LocationType.Water,
        LocationType.Water,
        waterTextures::getLandSouthEast);
    }
}

final class WaterWithLandNorthEast extends BaseGroundStrategy {

    @Inject
    public WaterWithLandNorthEast(WaterTextures waterTextures) {
        super(LocationType.Ground, LocationType.Water, LocationType.Water, LocationType.Ground,
        LocationType.Water,
        waterTextures::getLandNorthWest);
    }
}

final class WaterWithLandNorthSouthEast extends BaseGroundStrategy {

    @Inject
    public WaterWithLandNorthSouthEast(WaterTextures waterTextures) {
        super(LocationType.Ground, LocationType.Ground, LocationType.Ground, LocationType.Water,
        LocationType.Water,
        waterTextures::getLandNorthSouthEast);
    }
}

final class Ground implements ITileStrategy {

    private final WaterTextures textures;

    @Inject
    public Ground(WaterTextures waterTextures) {
        this.textures = waterTextures;
    }

    @Override
    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborThis] != LocationType.Ground) return false;
        return true;

    }

    @Override
    public TextureHolder execute(LocationType[] neighbors) {
        return textures.getGround();
    }
}

final class Water extends BaseGroundStrategy {

    @Inject
    public Water(WaterTextures waterTextures) {
        super(LocationType.Water, LocationType.Water, LocationType.Water, LocationType.Water,
        LocationType.Water,
        waterTextures::getSea);
    }
}


abstract class BaseGroundStrategy implements ITileStrategy {
    private final LocationType north;
    private final LocationType south;
    private final LocationType east;
    private final LocationType west;
    private final LocationType self;
    private final Supplier<TextureHolder> supplier;

    @Inject
    protected BaseGroundStrategy(
        LocationType north, LocationType south, LocationType east, LocationType west,
        LocationType self,
        Supplier<TextureHolder> supplier) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.self = self;
        this.supplier = supplier;
    }

    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborNorth] != north) return false;
        if (neighbors[Directions.NeighborSouth] != south) return false;
        if (neighbors[Directions.NeighborEast] != east) return false;
        if (neighbors[Directions.NeighborWest] != west) return false;
        if (neighbors[Directions.NeighborThis] != self) return false;
        return true;

    }

    public TextureHolder execute(LocationType[] neighbors) {
        return supplier.get();
    }
}