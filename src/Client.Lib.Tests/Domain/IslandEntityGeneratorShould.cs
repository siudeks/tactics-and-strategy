using System.Linq;
using Xunit;

namespace Game.Domain
{
    public sealed class IslandEntityGeneratorShould
    {
        /// <summary>
        /// Generateds random island where centre of island is insluded in generated model.
        /// </summary>
        [Fact]
        public void GenerateIslandWhereCentreIsDefined()
        {
            var island = IslandEntityGenerator.Random(new GeoPoint(10, 10));

            // The centre of the island need to be covered by island.
            var points = island.GeneratePoints().ToArray();
            Assert.True(points.Contains(new GeoPoint(10, 10)));
        }

        /// <summary>
        /// Generateds random island where group of points is included in generated model.
        /// </summary>
        [Fact]
        public void GenerateIslandWithPredefinedPoints()
        {
            var island = IslandEntityGenerator.Random(new GeoPoint(10, 10), new GeoPoint(100, 100));

            // The centre of the island need to be covered by island.
            Assert.True(island.GeneratePoints().Contains(new GeoPoint(10, 10)));
            Assert.True(island.GeneratePoints().Contains(new GeoPoint(100, 100)));
        }

        /// <summary>
        /// 0 - - - - >
        /// | x x x
        /// | x x x
        /// | x x x
        /// |
        /// v
        /// </summary>
        [Fact]
        public void GenerateIslandCase1()
        {
            var island = new IslandEntity
            {
                Corners = new[] { new GeoPoint(1, 1), new GeoPoint(3, 1), new GeoPoint(3, 3), new GeoPoint(1, 3) }
            };

            var points = island.GeneratePoints();

            Assert.True(points.Contains(new GeoPoint(1, 1)));
            Assert.True(points.Contains(new GeoPoint(2, 1)));
            Assert.True(points.Contains(new GeoPoint(3, 1)));
            Assert.True(points.Contains(new GeoPoint(1, 2)));
            Assert.True(points.Contains(new GeoPoint(2, 2)));
            Assert.True(points.Contains(new GeoPoint(3, 2)));
            Assert.True(points.Contains(new GeoPoint(1, 3)));
            Assert.True(points.Contains(new GeoPoint(2, 3)));
            Assert.True(points.Contains(new GeoPoint(3, 3)));
        }
    }
}
