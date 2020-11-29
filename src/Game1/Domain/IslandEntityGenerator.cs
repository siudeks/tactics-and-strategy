using System;
using System.Collections.Generic;

namespace Game.Domain
{
    /// <summary>
    /// Holds methods related to generating an island.
    /// </summary>
    public static class IslandEntityGenerator
    {
        /// <summary>
        /// Generates random island where some defined points need to be included in island as a 'centre'. 
        /// </summary>
        /// <param name="center">Centre of the island.</param>
        /// <returns>Definition of generated island.</returns>
        public static IslandEntity Random(params GeoPoint[] center)
        {
            var random = new Random();

            var corners = new List<GeoPoint>();
            // Start from negative angle to randomize more island generation. We don't know
            // what would be the first angle because it starts from -100 with random incrementation.
            // Only angles between [0; 360) will be included in map generation.
            int currentAngle = -100;
            while (true)
            {
                var actual = random.Next(30) + 30;
                var length = random.Next(10) + 5;

                currentAngle += actual;
                if (currentAngle >= 360) break;
                if (currentAngle < 0) continue;

                var x = (int)(Math.Cos(currentAngle * Math.PI / 360 * 2) * length);
                var y = (int)(Math.Sin(currentAngle * Math.PI / 360 * 2) * length);

                var candidate = default(GeoPoint);
                var distance = 0d;
                for (int j = 0; j < center.Length; j++)
                {
                    var point = new GeoPoint(x + center[j].X, y + center[j].Y);
                    var distanceTotal = 0d;
                    for (int i = 0; i < center.Length; i++)
                    {
                        var testedCenter = center[i];
                        var testedDistance = Math.Sqrt(Math.Pow(point.X - testedCenter.X, 2) + Math.Pow(point.Y - testedCenter.Y, 2));
                        distanceTotal += testedDistance;
                    }
                    if (distanceTotal > distance)
                    {
                        distance = distanceTotal;
                        candidate = point;
                    }
                }
                corners.Add(candidate);
            }

            return new IslandEntity { Corners = corners.ToArray() };
        }
    }
}
