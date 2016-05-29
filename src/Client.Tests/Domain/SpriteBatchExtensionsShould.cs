using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;

namespace Client.Domain
{
    [TestClass]
    public sealed class IslandEntityExtensionsShould
    {
        [TestMethod]
        public void DrawIsland()
        {
            var island = new IslandEntity
            {
                Corners = new[] { new GeoPoint(0, 0), new GeoPoint(2, 0), new GeoPoint(2, 2), new GeoPoint(0, 2) }
            };

            var commands = new[] { island }.Draw();

            Assert.AreEqual(commands.Length, 9);
        }

    }
}
