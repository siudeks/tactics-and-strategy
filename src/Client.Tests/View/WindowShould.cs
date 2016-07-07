using Client.Domain;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using Assert = NUnit.Framework.Assert;

namespace Client.View
{
    [TestClass]
    public sealed class WindowShould
    {
        [TestMethod]
        public void UseProperTextureForIsland()
        {
            var ground = new TextureHolder();
            var window = new Window(WaterTextures(), ground, null);

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddIsland(island);

            Assert.That(window[1, 1], Is.EqualTo(ground));
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

            Assert.That(window[1, 1], Is.EqualTo(city));
        }

        [TestMethod]
        public void UseProperWaterTexturesForIslandBorders()
        {
            var waterTextures = WaterTextures();
            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            var window = new Window(waterTextures, null, null);
            window.AddIsland(island);

            Assert.That(window[1, 2], Is.EqualTo(waterTextures[DirectionEnum.Top]));
            Assert.That(window[1, 0], Is.EqualTo(waterTextures[DirectionEnum.Down]));
            Assert.That(window[0, 1], Is.EqualTo(waterTextures[DirectionEnum.Left]));
            Assert.That(window[2, 1], Is.EqualTo(waterTextures[DirectionEnum.Right]));
            Assert.That(window[2, 2], Is.EqualTo(waterTextures[DirectionEnum.TopRight]));
            Assert.That(window[0, 2], Is.EqualTo(waterTextures[DirectionEnum.TopLeft]));
            Assert.That(window[2, 0], Is.EqualTo(waterTextures[DirectionEnum.DownRight]));
            Assert.That(window[0, 0], Is.EqualTo(waterTextures[DirectionEnum.DownLeft]));
        }

        private static Dictionary<DirectionEnum, TextureHolder> WaterTextures()
        {
            var result = new Dictionary<DirectionEnum, TextureHolder>();
            foreach (DirectionEnum direction in Enum.GetValues(typeof(DirectionEnum)))
            {
                result.Add(direction, new TextureHolder());
            }
            return result;
        }
    }
}
