using Client.Domain;
using System.Collections.Generic;

namespace Client.View
{
    /// <summary>
    /// Contains all entities which need to be visible in Game window and converts them
    /// to textures.
    /// </summary>
    public sealed class Window
    {
        private readonly Dictionary<GeoPoint, LocationType> points = new Dictionary<GeoPoint, LocationType>();

        private const int NeighborTopLeft = 0;
        private const int NeighborNorth = 1;
        private const int NeighborTopRight = 2;
        private const int NeighborWest = 3;
        private const int NeighborThis = 4;
        private const int NeighborEast = 5;
        private const int NeighborDownLeft = 6;
        private const int NeighborSouth = 7;
        private const int NeighborDownRight = 8;
        // defines single texture generator strategy
        // area: small array 3*3, where central point defines the point which need th have
        // generated texture, and the rest of array items defines neighbers with indexes as constatns above.
        // if strategy can't generate texture, returns defaultTexture.
        private delegate TextureHolder Generator(LocationType[] area, TextureHolder defaultTexture);

        private WaterTextures water;
        private TextureHolder ground;
        private TextureHolder city;
        private List<Generator> strategies = new List<Generator>();

        public Window(WaterTextures water, TextureHolder ground, TextureHolder city)
        {
            this.water = water;
            this.ground = ground;
            this.city = city;

            strategies.Add(StrategyForCity);
            strategies.Add(StrategyForGround);
            strategies.Add(CoastWithLandToTheNorth);
            strategies.Add(CoastWithLandToTheSouth);
            strategies.Add(CoastWithLandToTheWest);
            strategies.Add(CoastWithLandToTheEast);
            strategies.Add(CoastWithLandToTheNorthEast);
            strategies.Add(CoastWithLandToTheNorthWest);
            strategies.Add(CoastWithLandToTheSouthEast);
            strategies.Add(CoastWithLandToTheSouthWest);
            strategies.Add(NullStrategy);
        }

        public void AddIsland(IslandEntity island)
        {
            foreach (var item in island.GeneratePoints())
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

                    area[NeighborTopLeft] = points.ContainsKey(centerOfArea.TopLeft()) ? points[centerOfArea.TopLeft()] : LocationType.Water;
                    area[NeighborNorth] = points.ContainsKey(centerOfArea.Top()) ? points[centerOfArea.Top()] : LocationType.Water;
                    area[NeighborTopRight] = points.ContainsKey(centerOfArea.TopRight()) ? points[centerOfArea.TopRight()] : LocationType.Water;
                    area[NeighborWest] = points.ContainsKey(centerOfArea.Left()) ? points[centerOfArea.Left()] : LocationType.Water;
                    area[NeighborThis] = points.ContainsKey(centerOfArea) ? points[centerOfArea] : LocationType.Water;
                    area[NeighborEast] = points.ContainsKey(centerOfArea.Right()) ? points[centerOfArea.Right()] : LocationType.Water;
                    area[NeighborDownLeft] = points.ContainsKey(centerOfArea.DownLeft()) ? points[centerOfArea.DownLeft()] : LocationType.Water;
                    area[NeighborSouth] = points.ContainsKey(centerOfArea.Down()) ? points[centerOfArea.Down()] : LocationType.Water;
                    area[NeighborDownRight] = points.ContainsKey(centerOfArea.DownRight()) ? points[centerOfArea.DownRight()] : LocationType.Water;

                    var centerTexture = new TextureHolder();
                    foreach (var strategy in strategies)
                    {
                        var texture = strategy(area, centerTexture);
                        if (texture == centerTexture) continue;

                        centerTexture = texture;
                        break;
                    }

                    yield return new PointContext(centerOfArea, centerTexture);
                }
        }

        private TextureHolder StrategyForCity(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborThis] == LocationType.City) return city;

            return defaultValue;
        }

        private TextureHolder StrategyForGround(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborThis] == LocationType.Ground) return ground;

            return defaultValue;
        }

        private TextureHolder CoastWithLandToTheNorth(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheNorth;
        }

        private TextureHolder CoastWithLandToTheSouth(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheSouth;
        }

        private TextureHolder CoastWithLandToTheWest(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheWest;
        }

        private TextureHolder CoastWithLandToTheEast(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheEast;
        }

        private TextureHolder CoastWithLandToTheNorthEast(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheNorthEast;
        }

        private TextureHolder CoastWithLandToTheNorthWest(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheNorthWest;
        }

        private TextureHolder CoastWithLandToTheSouthEast(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheSouthEast;
        }

        private TextureHolder CoastWithLandToTheSouthWest(LocationType[] neighbors, TextureHolder defaultValue)
        {
            if (neighbors[NeighborWest] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborEast] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborNorth] != LocationType.Water) return defaultValue;
            if (neighbors[NeighborSouth] == LocationType.Water) return defaultValue;
            if (neighbors[NeighborThis] != LocationType.Water) return defaultValue;

            return water.CoastWithLandToTheSouthWest;
        }

        // should be invoked at the end of strategies
        private TextureHolder NullStrategy(LocationType[] neighbors, TextureHolder defaultValue)
        {
            return water.Sea;
        }

        private enum LocationType
        {
            Water,
            City,
            Ground,
            LandUnit
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
