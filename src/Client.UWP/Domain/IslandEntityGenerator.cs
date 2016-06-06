using System;
using System.Collections.Generic;

namespace Client.Domain
{
    public static class IslandEntityGenerator
    {
        public static IslandEntity Random()
        {
            var random = new Random();

            var corners = new List<GeoPoint>();
            int currentAngle = 0;
            while (currentAngle < 360)
            {
                var actual = random.Next(30) + 30;
                var length = random.Next(10) + 5;

                currentAngle += actual;
                var x = (int) (Math.Cos(currentAngle * Math.PI / 360 * 2 ) * length);
                var y = (int) (Math.Sin(currentAngle * Math.PI / 360 * 2) * length);
                var point = new GeoPoint(x, y);
                corners.Add(point);
            }

            return new IslandEntity { Corners = corners.ToArray() };
        }
    }
}
