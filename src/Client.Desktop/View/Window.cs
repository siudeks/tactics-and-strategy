using Client.Domain;
using System.Collections.Generic;
using System;
using System.Linq;
using Microsoft.Xna.Framework;
using Client.Resources;
using Microsoft.Xna.Framework.Graphics;

namespace Client.View
{
    /// <summary>
    /// Contains all entities which need to be visible in Game window and converts them
    /// to textures.
    /// </summary>
    public sealed class Window : IGameComponent,
                                 ITextureConsumer
    {
        private readonly Dictionary<GeoPoint, LocationType> points = new Dictionary<GeoPoint, LocationType>();

        // defines single texture generator strategy
        // area: small array 3*3, where central point defines the point which need th have
        // generated texture, and the rest of array items defines neighbers with indexes as constatns above.
        // if strategy can't generate texture, returns defaultTexture.
        private delegate TextureHolder Generator(LocationType[] area, TextureHolder defaultTexture);

        private WaterTextures water;
        private TextureHolder city;
        private List<Generator> functionStrategies = new List<Generator>();
        private ITileStrategy[] strategies = new ITileStrategy[0];
        private ITileStrategy fallbackStrategy;

        public event EventHandler<EventArgs> DrawOrderChanged;
        public event EventHandler<EventArgs> VisibleChanged;

        public int DrawOrder => 0;

        public bool Visible => true;

        public void Initialize(WaterTextures water, TextureHolder city, ITileStrategy fallbackStrategy, params ITileStrategy[] strategies)
        {
            this.water = water;
            this.city = city;

            functionStrategies.Add(CoastWithLandToTheWest);
            functionStrategies.Add(CoastWithLandToTheEast);
            functionStrategies.Add(CoastWithLandToTheNorthEast);
            functionStrategies.Add(CoastWithLandToTheNorthWest);
            functionStrategies.Add(CoastWithLandToTheSouthEast);
            functionStrategies.Add(CoastWithLandToTheSouthWest);

            this.strategies = strategies;
            this.fallbackStrategy = fallbackStrategy;
        }

        public void AddIsland(IslandEntity island)
        {
            foreach (var item in island.GeneratePoints().ToArray())
            {
                var point = new GeoPoint(item.X, item.Y);
                points.Add(point, LocationType.Ground);
            }
        }

        /// <summary>
        /// Defines a city on the map.
        /// 
        /// Needs to be invoked later then <see cref="AddIsland(IslandEntity)"/>
        /// </summary>
        /// <param name="entity"></param>
        public void AddCity(CityEntity entity)
        {
            var point = new GeoPoint(entity.X, entity.Y);
            points[point] = LocationType.City;
        }

        public void Include(LandUnitEntity entity)
        {
            var point = new GeoPoint(entity.X, entity.Y);
            points[point] = LocationType.LandUnit;
        }

        /// <summary>
        /// Generates list of textures for given square, when left bottom corner is provided.
        /// </summary>
        /// <param name="lbx"></param>
        /// <param name="lby"></param>
        /// <param name="dx"></param>
        /// <param name="dy"></param>
        /// <returns>Sequence contains textures, starting from left bottom corner and returning rows first.</returns>
        public IEnumerable<PointContext> GetWindow(int lbx, int lby, int dx, int dy)
        {
            for (int y = 0; y < dy; y++)
                for (int x = 0; x < dx; x++)
                {
                    var geox = lbx + x;
                    var geoy = lby + y;

                    var area = new LocationType[9];
                    var centerOfArea = new GeoPoint(geox, geoy);

                    for (int i = 0; i < 9; i++) area[i] = LocationType.Water;

                    area[Directions.NeighborTopLeft] = points.ContainsKey(centerOfArea.TopLeft()) ? points[centerOfArea.TopLeft()] : LocationType.Water;
                    area[Directions.NeighborNorth] = points.ContainsKey(centerOfArea.Top()) ? points[centerOfArea.Top()] : LocationType.Water;
                    area[Directions.NeighborTopRight] = points.ContainsKey(centerOfArea.TopRight()) ? points[centerOfArea.TopRight()] : LocationType.Water;
                    area[Directions.NeighborWest] = points.ContainsKey(centerOfArea.Left()) ? points[centerOfArea.Left()] : LocationType.Water;
                    area[Directions.NeighborThis] = points.ContainsKey(centerOfArea) ? points[centerOfArea] : LocationType.Water;
                    area[Directions.NeighborEast] = points.ContainsKey(centerOfArea.Right()) ? points[centerOfArea.Right()] : LocationType.Water;
                    area[Directions.NeighborDownLeft] = points.ContainsKey(centerOfArea.DownLeft()) ? points[centerOfArea.DownLeft()] : LocationType.Water;
                    area[Directions.NeighborSouth] = points.ContainsKey(centerOfArea.Down()) ? points[centerOfArea.Down()] : LocationType.Water;
                    area[Directions.NeighborDownRight] = points.ContainsKey(centerOfArea.DownRight()) ? points[centerOfArea.DownRight()] : LocationType.Water;

                    var centerTexture = new TextureHolder();
                    var handled = false;
                    foreach (var strategy in functionStrategies)
                    {
                        var texture = strategy(area, centerTexture);
                        if (texture == centerTexture) continue;

                        centerTexture = texture;
                        handled = true;
                        break;
                    }

                    if (!handled)
                    {
                        foreach (var strategy in strategies)
                        {
                            if (!strategy.CanExecute(area)) continue;

                            centerTexture = strategy.Execute(area);
                            handled = true;
                            break;
                        }
                    }

                    if (!handled) centerTexture = fallbackStrategy.Execute(area);

                    yield return new PointContext(centerOfArea, centerTexture);
                }
        }

        private TextureHolder CoastWithLandToTheWest(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[Directions.NeighborWest] != LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborEast] == LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheWest;
        }

        private TextureHolder CoastWithLandToTheEast(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[Directions.NeighborWest] == LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborEast] != LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[Directions.NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheEast;
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

        Texture2D terrainTexture;
        Texture2D desertRatsTextures;

        public void OnLoaded(Texture2D texture, TextureItem item)
        {
            if (item == TextureItem.TERRAIN) terrainTexture = texture;
            if (item == TextureItem.DESERT_RATES) desertRatsTextures = texture;
        }

        public void LoadFinished()
        {
            var waterTextures = new WaterTextures(terrainTexture);
            var cityTexture = new TextureHolder(terrainTexture, new Rectangle(7 * Config.SpriteSize, 9 * Config.SpriteSize, Config.SpriteSize, Config.SpriteSize));
            var groundTexture = new TextureHolder(terrainTexture, new Rectangle(0 * Config.SpriteSize, 0, Config.SpriteSize, Config.SpriteSize));
            var landUnitTexture = new TextureHolder(desertRatsTextures, new Rectangle(1 + 0 * Config.SpriteSize, 1 + 0, Config.SpriteSize, Config.SpriteSize));

            Initialize(waterTextures, cityTexture, new DefaultStrategy(waterTextures.Sea),
                new CoastWithLandToTheNorthStrategy(waterTextures.CoastWithLandToTheNorth),
                new CoastWithLandToTheSouthStrategy(waterTextures.CoastWithLandToTheSouth),
                new GroundStrategy(groundTexture),
                new LandUnitStrategy(landUnitTexture),
                new CityStrategy(cityTexture));
        }



        /// <summary>
        /// Holds together geopoint and texture used for that point to draw it.
        /// </summary>
        public struct PointContext
        {
            public readonly GeoPoint GeoPoint;
            public readonly TextureHolder Texture;

            public PointContext(GeoPoint geopoint, TextureHolder texture)
            {
                GeoPoint = geopoint;
                Texture = texture;
            }
        }
    }
}
