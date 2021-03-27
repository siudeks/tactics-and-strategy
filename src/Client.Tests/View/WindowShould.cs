using Client.Domain;
using FluentAssertions;
using System.Linq;
using Xunit;

namespace Client.View
{
    public sealed class WindowShould
    {
        [Fact]
        public void UseProperTextureForIsland()
        {
            var ground = new TextureHolder();
            var window = new Window(new WaterTextures(), ground, null, new GroundStrategy(ground));

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddIsland(island);

            var view = window.GetWindow(1, 1, 1, 1);
            view.First().Texture.Should().Be(ground);
        }

        /// <summary>
        /// When a city need to be drawn, its texture should be used instead of
        /// terrain located behind the city.
        /// </summary>
        [Fact]
        public void CityTakesPrecedenceOnTerrain()
        {
            var ground = new TextureHolder();
            var city = new TextureHolder();
            var window = new Window(null, city, null, new GroundStrategy(ground), new CityStrategy(city));

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddIsland(island);
            window.AddCity(new CityEntity(1, 1));

            var view = window.GetWindow(1, 1, 1, 1).ToArray();
            view.First().Texture.Should().Be(city);
        }

        /// <summary>
        /// When a land unit need to be drawn, its texture should be used instead of
        /// terrain / city located behind the unit.
        /// </summary>
        [Fact]
        public void LandUnitTakesPrecedenceOnTerrain()
        {
            var ground = new TextureHolder();
            var city = new TextureHolder();
            var landUnit = new TextureHolder();
            var window = new Window(null, city, new DefaultStrategy(null), new LandUnitStrategy(landUnit));

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddCity(new CityEntity(1, 1));
            window.Include(new LandUnitEntity(1, 1));

            var view = window.GetWindow(1, 1, 1, 1).ToArray();
            view.First().Texture.Should().Be(landUnit);
        }

        [Fact]
        public void UseProperWaterTexturesForCoastWithLandToTheNorthAndSouthAndWestAndEast()
        {
            var waterTextures = new WaterTextures();

            // small map for test:
            // O?O
            // ?X?
            // O?O 
            // where O - water, X - island, ? - water where we test textures.
            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            var window = new Window(waterTextures, null, new DefaultStrategy(waterTextures.Sea), 
                new CoastWithLandToTheNorthStrategy(waterTextures.CoastWithLandToTheNorth),
                new CoastWithLandToTheSouthStrategy(waterTextures.CoastWithLandToTheSouth));
            window.AddIsland(island);

            var view = window.GetWindow(0, 0, 3, 3).ToArray();

            view[2 * 3 + 1].Texture.Should().Be(waterTextures.CoastWithLandToTheNorth);
            view[0 * 3 + 1].Texture.Should().Be(waterTextures.CoastWithLandToTheSouth);
            view[1 * 3 + 0].Texture.Should().Be(waterTextures.CoastWithLandToTheWest);
            view[1 * 3 + 2].Texture.Should().Be(waterTextures.CoastWithLandToTheEast);
        }

        [Fact]
        public void UseProperWaterTexturesForCoastWithLandToTheNorthEastAndNorthWestAndSouthEastAndSouthWest()
        {
            var waterTextures = new WaterTextures();

            // small map for test:
            // ?X?
            // XXX
            // ?X? 
            // where O - water, X - island, ? - water where we test textures.
            var window = new Window(waterTextures, null, new DefaultStrategy(waterTextures.Sea));
            window.AddIsland(new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 2 } } });
            window.AddIsland(new IslandEntity { Corners = new[] { new GeoPoint { X = 0, Y = 1 }, new GeoPoint { X = 2, Y = 1 } } });
            window.AddIsland(new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 0 } } });

            var view = window.GetWindow(0, 0, 3, 3).ToArray();

            view[0 * 3 + 0].Texture.Should().Be(waterTextures.CoastWithLandToTheNorthEast);
            view[0 * 3 + 2].Texture.Should().Be(waterTextures.CoastWithLandToTheNorthWest);
            view[2 * 3 + 0].Texture.Should().Be(waterTextures.CoastWithLandToTheSouthEast);
            view[2 * 3 + 2].Texture.Should().Be(waterTextures.CoastWithLandToTheSouthWest);
        }
    }
}
