using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System.Collections.Generic;

namespace Client.Domain
{
    [TestClass]
    public sealed class IslandEntityShould
    {
        /// <summary>
        /// Simple test. As proof of working, we need to simple island wit corners defined as :
        /// [ {0, 0}, {2, 0}, {2, 2}, {0, 2}], or - if you prefer - in more sexy look
        /// xox
        /// ooo
        /// xox
        /// where x is the island's corner.
        /// expected is list of all island tiles, it means 
        /// [ {0, 0}, {0, 1}, {0, 2}, ... {2, 2} ] or - again - more sexy presentation
        /// xxx
        /// xxx
        /// xxx
        /// </summary>
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

            CollectionAssert.AreEquivalent(expected, actual);
        }

    }
}
