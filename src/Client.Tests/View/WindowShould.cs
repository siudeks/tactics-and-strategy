using Client.Domain;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using NUnit.Framework;
using Assert = NUnit.Framework.Assert;

namespace Client.View
{
    [TestClass]
    public sealed class WindowShould
    {
        [TestMethod]
        public void UseProperTextureForIsland()
        {
            var water = new TextureHolder();
            var ground = new TextureHolder();
            var window = new Window(water, ground, null);

            var island = new IslandEntity { Corners = new[] { new GeoPoint { X = 1, Y = 1 } } };
            window.AddIsland(island);

            Assert.That(window[0, 0], Is.EqualTo(water));
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
    }
}
