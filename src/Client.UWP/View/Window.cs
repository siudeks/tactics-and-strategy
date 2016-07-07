using Client.Domain;
using System.Collections.Generic;

namespace Client.View
{
    /// <summary>
    /// Controls logical items which exists on visible part of game 
    /// to Textures.
    /// </summary>
    public sealed class Window
    {
        private readonly Dictionary<GeoPoint, LocationType> points = new Dictionary<GeoPoint, LocationType>();

        private Dictionary<DirectionEnum, TextureHolder> water;
        private TextureHolder ground;
        private TextureHolder city;
        public Window(Dictionary<DirectionEnum, TextureHolder> water, TextureHolder ground, TextureHolder city)
        {
            this.water = water;
            this.ground = ground;
            this.city = city;
        }

        public void AddIsland(IslandEntity island)
        {
            foreach (var item in island.GeneratePoints())
            {
                var point = new GeoPoint(item.X, item.Y);
                points.Add(point, LocationType.Ground);
            }
        }

        public void AddCity(CityEntity entity)
        {
            var point = new GeoPoint(entity.X, entity.Y);
            points[point] = LocationType.City;
        }

        /// <summary>
        /// Returns Texture for given location.
        /// </summary>
        /// <param name="geox"></param>
        /// <param name="geoy"></param>
        /// <returns></returns>
        public TextureHolder this[int geox, int geoy]
        {
            get
            {
                var index = new GeoPoint(geox, geoy);
                if (points.ContainsKey(index))
                    switch (points[index])
                    {
                        case LocationType.City:
                            return city;
                        case LocationType.Ground:
                            return ground;
                    }

                return water[DirectionEnum.Unknown];
            }
        }

        private enum LocationType
        {
            Water,
            City,
            Ground
        }
    }
}
