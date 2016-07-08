using Client.Domain;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using Assert = NUnit.Framework.Assert;
using System.Linq;

namespace Client.View
{
    [TestClass]
    public sealed class WindowShould
    {
        [TestMethod]
        public void UseProperTextureForIsland()
        {
            var ground = new TextureHolder();
            var window = new Window(new WaterTextures(), ground, null);

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddIsland(island);

            var view = window.GetWindow(1, 1, 1, 1);
            Assert.That(view.First().Texture, Is.EqualTo(ground));
        }

        [TestMethod]
        public void CityTakesPrecedenceOnTerrain()
        {
            var ground = new TextureHolder();
            var city = new TextureHolder();
            var window = new Window(null, ground, city);

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddIsland(island);
            window.AddCity(new CityEntity(1, 1));

            var view = window.GetWindow(1, 1, 1, 1).ToArray();
            Assert.That(view.First().Texture, Is.EqualTo(city));
        }

        [TestMethod]
        public void UseProperWaterTexturesForCoastWithLandToTheNorthAndSouthAndWestAndEast()
        {
            var waterTextures = new WaterTextures();

            // small map for test:
            // OOO
            // OXO
            // OOO 
            // where O - water, X - island
            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            var window = new Window(waterTextures, null, null);
            window.AddIsland(island);

            var view = window.GetWindow(0, 0, 3, 3).ToArray();

            Assert.That(view[2 * 3 + 1].Texture, Is.EqualTo(waterTextures.CoastWithLandToTheNorth));
            Assert.That(view[0 * 3 + 1].Texture, Is.EqualTo(waterTextures.CoastWithLandToTheSouth));
            Assert.That(view[1 * 3 + 0].Texture, Is.EqualTo(waterTextures.CoastWithLandToTheWest));
            Assert.That(view[1 * 3 + 2].Texture, Is.EqualTo(waterTextures.CoastWithLandToTheEast));
        }
    }
}
