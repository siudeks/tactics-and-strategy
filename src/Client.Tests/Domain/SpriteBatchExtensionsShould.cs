﻿using FluentAssertions;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;

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

            actual.Should().Equal(expected, (o1, o2) => o1.X == o2.X && o1.Y == o2.Y);
        }

    }
}
