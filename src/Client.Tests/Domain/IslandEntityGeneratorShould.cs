﻿using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System.Linq;

namespace Client.Domain
{
    [TestClass]
    public sealed class IslandEntityGeneratorShould
    {
        /// <summary>
        /// Generateds random island where centre of island is insluded in generated model.
        /// </summary>
        [TestMethod]
        public void GenerateIslandWhereCentreIsDefined()
        {
            var island = IslandEntityGenerator.Random(new GeoPoint(10, 10));

            // The centre of the island need to be covered by island.
            Assert.IsTrue(island.GeneratePoints().Contains(new GeoPoint(10, 10)));
        }

        /// <summary>
        /// Generateds random island where group of points is included in generated model.
        /// </summary>
        [TestMethod]
        public void GenerateIslandWithPredefinedPoints()
        {
            var island = IslandEntityGenerator.Random(new GeoPoint(10, 10), new GeoPoint(100, 100));

            // The centre of the island need to be covered by island.
            Assert.IsTrue(island.GeneratePoints().Contains(new GeoPoint(10, 10)));
            Assert.IsTrue(island.GeneratePoints().Contains(new GeoPoint(100, 100)));
        }

        /// <summary>
        /// 0 - - - - >
        /// | x x x
        /// | x x x
        /// | x x x
        /// |
        /// v
        /// </summary>
        [TestMethod]
        public void GenerateIslandCase1()
        {
            var island = new IslandEntity
            {
                Corners = new[] { new GeoPoint(1, 1), new GeoPoint(3, 1), new GeoPoint(3, 3), new GeoPoint(1, 3) }
            };

            var points = island.GeneratePoints();

            Assert.IsTrue(points.Contains(new GeoPoint(1, 1)));
            Assert.IsTrue(points.Contains(new GeoPoint(2, 1)));
            Assert.IsTrue(points.Contains(new GeoPoint(3, 1)));
            Assert.IsTrue(points.Contains(new GeoPoint(1, 2)));
            Assert.IsTrue(points.Contains(new GeoPoint(2, 2)));
            Assert.IsTrue(points.Contains(new GeoPoint(3, 2)));
            Assert.IsTrue(points.Contains(new GeoPoint(1, 3)));
            Assert.IsTrue(points.Contains(new GeoPoint(2, 3)));
            Assert.IsTrue(points.Contains(new GeoPoint(3, 3)));
        }
    }
}