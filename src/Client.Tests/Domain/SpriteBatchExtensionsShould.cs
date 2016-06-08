using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using Assert = NUnit.Framework.Assert;

namespace Client.Domain
{
    [TestClass]
    public sealed class IslandEntityExtensionsShould
    {
        [TestMethod]
        public void GeneratePoints()
        {
            var island = new IslandEntity
            {
                Corners = new[] { new GeoPoint(0, 0), new GeoPoint(2, 0), new GeoPoint(2, 2), new GeoPoint(0, 2) }
            };

            var actual = island.GeneratePoints();

            var expected = new List<GeoPoint>();
            for (int x = 0; x < 3; x++)
                for (int y = 0; y < 3; y++)
                    expected.Add(new GeoPoint { X = x, Y = y });

            Func<dynamic, dynamic, bool> cmp = (o1, o2) => o1.X == o2.X && o1.Y == o2.Y;

            Assert.That(actual, Is.EquivalentTo(expected).Using(cmp));
        }

    }
}
