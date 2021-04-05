package com.mygdx.game.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Config;
import com.mygdx.game.domain.CityEntity;
import com.mygdx.game.domain.IslandEntity;
import com.mygdx.game.domain.IslandEntityExtensions;
import com.mygdx.game.domain.LandUnitEntity;
import com.mygdx.game.domain.GeoPoint;
import com.mygdx.game.domain.IntendedMapCentre;
import com.mygdx.game.domain.IslandEntityGenerator;
import com.mygdx.game.resources.ITextureConsumer;
import com.mygdx.game.resources.WaterTextures;
import com.mygdx.game.runtime.GameComponent;
import com.mygdx.game.runtime.IBatchDrawer;

import com.mygdx.game.resources.TextureItem;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Map;
import io.vavr.collection.HashMap;

/**
 * Contains all entities which need to be visible in Game window and converts them
 * to textures.
 */
public final class Window implements GameComponent,
                                     IBatchDrawer,
                                     ITextureConsumer
{
    private Map<GeoPoint, LocationType> mapPoints = HashMap.<GeoPoint, LocationType>empty();

    // defines single texture generator strategy
    // area: small array 3*3, where central point defines the point which need th have
    // generated texture, and the rest of array items defines neighbers with indexes as constatns above.
    // if strategy can't generate texture, returns defaultTexture.
    private interface Generator {
        TextureHolder apply(LocationType[] area, TextureHolder defaultTexture);
    }

    private WaterTextures water;
    private TextureHolder city;
    private List<Generator> functionStrategies = List.<Generator>empty();
    private ITileStrategy[] strategies = new ITileStrategy[0];
    private ITileStrategy fallbackStrategy;

    public void Initialize(WaterTextures water, TextureHolder city, ITileStrategy fallbackStrategy, ITileStrategy... strategies) {
        this.water = water;
        this.city = city;

        functionStrategies = functionStrategies
            .append(this::CoastWithLandToTheWest)
            .append(this::CoastWithLandToTheEast)
            .append(this::CoastWithLandToTheNorthEast)
            .append(this::CoastWithLandToTheNorthWest)
            .append(this::CoastWithLandToTheSouthEast)
            .append(this::CoastWithLandToTheSouthWest);

        this.strategies = strategies;
        this.fallbackStrategy = fallbackStrategy;
    }

    public void AddIsland(IslandEntity island) {
        for (var item : IslandEntityExtensions.GeneratePoints(island)) {
            var point = new GeoPoint(item.X, item.Y);
            mapPoints = mapPoints.put(point, LocationType.Ground);
        }
    }

    /**
     * Needs to be invoked later then <see cref="AddIsland(IslandEntity)"/>.
     * @param entity
     */
    public void AddCity(CityEntity entity)
    {
        var point = new GeoPoint(entity.x(), entity.y());
        mapPoints.put(point, LocationType.City);
    }

    /**
     * 
     * @param entity
     */
    public void Include(LandUnitEntity entity)
    {
        var point = new GeoPoint(entity.X(), entity.Y());
        mapPoints.put(point, LocationType.LandUnit);
    }

    /**
     * Generates list of textures for given square, when left bottom corner is provided.
     * @return Sequence contains textures, starting from left bottom corner and returning rows first
     */
    public Seq<PointContext> GetWindow(int lbcx, int lbcy, int width, int height) {
        var result = List.<PointContext>empty();
        for (int dy = 0; dy < height; dy++)
            for (int dx = 0; dx < width; dx++)
            {
                var geox = lbcx + dx;
                var geoy = lbcy + dy;

                var area = new LocationType[9];
                var centerOfArea = new GeoPoint(geox, geoy);
                var relativeCenterOfArea = new GeoPoint(dx, dy);

                for (int i = 0; i < 9; i++) area[i] = LocationType.Water;

                area[Directions.NeighborTopLeft] = mapPoints.get(centerOfArea.TopLeft()).getOrElse(LocationType.Water);
                area[Directions.NeighborNorth] = mapPoints.get(centerOfArea.Top()).getOrElse(LocationType.Water);
                area[Directions.NeighborTopRight] = mapPoints.get(centerOfArea.TopRight()).getOrElse(LocationType.Water);
                area[Directions.NeighborWest] = mapPoints.get(centerOfArea.Left()).getOrElse(LocationType.Water);
                area[Directions.NeighborThis] = mapPoints.get(centerOfArea).getOrElse(LocationType.Water);
                area[Directions.NeighborEast] = mapPoints.get(centerOfArea.Right()).getOrElse(LocationType.Water);
                area[Directions.NeighborDownLeft] = mapPoints.get(centerOfArea.DownLeft()).getOrElse(LocationType.Water);
                area[Directions.NeighborSouth] = mapPoints.get(centerOfArea.Down()).getOrElse(LocationType.Water);
                area[Directions.NeighborDownRight] = mapPoints.get(centerOfArea.DownRight()).getOrElse(LocationType.Water);

                var centerTexture = new TextureHolder();
                var handled = false;
                for (var strategy : functionStrategies) {
                    var texture = strategy(area, centerTexture);
                    if (texture == centerTexture) continue;

                    centerTexture = texture;
                    handled = true;
                    break;
                }

                if (!handled) {
                    for (var strategy : strategies) {
                        if (!strategy.CanExecute(area)) continue;

                        centerTexture = strategy.Execute(area);
                        handled = true;
                        break;
                    }
                }

                if (!handled) centerTexture = fallbackStrategy.Execute(area);

                result = result.append(new PointContext(relativeCenterOfArea, centerTexture));
            }
        return result;
    }

    private TextureHolder CoastWithLandToTheWest(LocationType[] neighbors, TextureHolder defaultValue)
    {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborEast] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

        return water.getCoastWithLandToTheWest();
    }

    private TextureHolder CoastWithLandToTheEast(LocationType[] neighbors, TextureHolder defaultValue)
    {
        if (neighbors[Directions.NeighborWest] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

        return water.getCoastWithLandToTheEast();
    }

    private TextureHolder CoastWithLandToTheNorthEast(LocationType[] neighbors, TextureHolder defaultValue)
    {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborEast] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborNorth] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

        return water.CoastWithLandToTheNorthEast;
    }

    private TextureHolder CoastWithLandToTheNorthWest(LocationType[] neighbors, TextureHolder defaultValue)
    {
        if (neighbors[Directions.NeighborWest] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborNorth] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

        return water.CoastWithLandToTheNorthWest;
    }

    private TextureHolder CoastWithLandToTheSouthEast(LocationType[] neighbors, TextureHolder defaultValue)
    {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborEast] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborSouth] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

        return water.CoastWithLandToTheSouthEast;
    }

    private TextureHolder CoastWithLandToTheSouthWest(LocationType[] neighbors, TextureHolder defaultValue)
    {
        if (neighbors[Directions.NeighborWest] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborSouth] == LocationType.Water) return defaultValue;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

        return water.CoastWithLandToTheSouthWest;
    }

    public void Initialize()
    {
        // temporar variables to keep sample textures for demo purposes.
        var island = IslandEntityGenerator.Random(new GeoPoint(20, 20));

        this.AddIsland(island);
        this.AddCity(new CityEntity(20, 20));
        this.Include(new LandUnitEntity(21, 20));
    }

    Texture terrainTexture;
    Texture desertRatsTextures;

    public void OnLoaded(Texture texture, TextureItem item) {
        if (item == TextureItem.TERRAIN) terrainTexture = texture;
        if (item == TextureItem.DESERT_RATES) desertRatsTextures = texture;
    }

    public void LoadFinished()
    {
        var waterTextures = new WaterTextures(terrainTexture);
        var cityTexture = new TextureHolder(terrainTexture, new Rectangle(7 * Config.SpriteSize, 9 * Config.SpriteSize, Config.SpriteSize, Config.SpriteSize));
        var groundTexture = new TextureHolder(terrainTexture, new Rectangle(0 * Config.SpriteSize, 0, Config.SpriteSize, Config.SpriteSize));
        var landUnitTexture = new TextureHolder(desertRatsTextures, new Rectangle(1 + 0 * Config.SpriteSize, 1 + 0, Config.SpriteSize, Config.SpriteSize));

        Initialize(waterTextures, cityTexture, new DefaultStrategy(waterTextures.getSea()),
            new CoastWithLandToTheNorthStrategy(waterTextures.getCoastWithLandToTheNorth()),
            new CoastWithLandToTheSouthStrategy(waterTextures.getCoastWithLandToTheSouth()),
            new GroundStrategy(groundTexture),
            new LandUnitStrategy(landUnitTexture),
            new CityStrategy(cityTexture));
    }

    public IntendedMapCentre IntendedMapCentre;

    public void OnDraw(SpriteBatch spriteBatch) {

        var x = IntendedMapCentre.X;
        var y = IntendedMapCentre.Y;

        // display sample island
        var points = this.GetWindow(0 + x, 0 + y, 30, 30);

        for (var it : points) {
            var position = new Vector2(
                it.GeoPoint.X * Config.SpriteSize,
                it.GeoPoint.Y * Config.SpriteSize);
            spriteBatch.Draw(position, it.Texture);
        }
    }



    /**
     * Holds together geopoint and texture used for that point to draw it.
     */
    public class PointContext {
        public final GeoPoint GeoPoint;
        public final TextureHolder Texture;

        public PointContext(GeoPoint geopoint, TextureHolder texture)
        {
            GeoPoint = geopoint;
            Texture = texture;
        }
    }
}
