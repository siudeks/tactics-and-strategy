using Client.Behaviors;
using Microsoft.Xna.Framework;
using System;
using System.Linq;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;

namespace Client.Domain
{
    public static class IslandEntityExtensions
    {
        public static SpriteDrawCommand[] Draw(this IslandEntity[] entries)
        {
            return entries
                .Select(it => FillPolygon(it.Corners))
                .SelectMany(it => it)
                .Select(o => new SpriteDrawCommand() { X = (int)o.X, Y = (int)o.Y })
                .ToArray();
        }

        // http://alienryderflex.com/polygon_fill/
        private static Vector2[] FillPolygon(GeoPoint[] corners)
        {
            // convert all verticles to edges.
            var edges = new Queue<GeoPoint[]>();
            var cornersCount = corners.Length;
            for (int i = 0; i < cornersCount; i++)
            {
                var next = i + 1;
                if (next == cornersCount) next = 0;

                edges.Enqueue(AsLine(corners[i].X, corners[i].Y, corners[next].X, corners[next].Y));
            }

            // prepare all points of edges for horizontal linescan
            var sortedEdges = edges
                .SelectMany(it => it)
                .OrderByDescending(it => it, new GeoPointComparer())
                .ToArray();

            // convert edges to horizontal lines which fill the shape.
            var lineStart = sortedEdges[0];
            var lineEnd = sortedEdges[0];
            var lines = new Queue<Tuple<GeoPoint, GeoPoint>>();
            for (int i = 1; i < sortedEdges.Length; i++)
            {
                // find the most right element in line and continue
                if (sortedEdges[i].Y == lineStart.Y)
                {
                    lineEnd = sortedEdges[i];
                    continue;
                }

                // hold just finished line and create new one
                lines.Enqueue(Tuple.Create(lineStart, lineEnd));
                lineStart = sortedEdges[i];
                lineEnd = sortedEdges[i];
            }
            lines.Enqueue(Tuple.Create(lineStart, lineEnd));

            // now in lines we have all horizonta lines, so let's fill the polygon
            var points = new Queue<Vector2>();
            foreach (var item in lines)
            {
                var liney = item.Item1.Y;

                //horizontal line contains at least initial point
                points.Enqueue(new Vector2(item.Item1.X, liney));

                // if initial point is the same as end point, need to go to the next line.
                if (item.Item1.X == item.Item2.X) break;

                for (int x = item.Item1.X + 1; x < item.Item2.X; x++)
                    points.Enqueue(new Vector2(x, liney));

                points.Enqueue(new Vector2(item.Item2.X, liney));
            }

            return points.ToArray();

        }

        // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
        // https://github.com/fragkakis/bresenham/blob/master/src/main/java/org/fragkakis/Bresenham.java    
        private static GeoPoint[] AsLine(int x0, int y0, int x1, int y1)
        {
            var line = new Queue<GeoPoint>();

            int dx = Math.Abs(x1 - x0);
            int dy = Math.Abs(y1 - y0);

            int sx = x0 < x1 ? 1 : -1;
            int sy = y0 < y1 ? 1 : -1;

            int err = dx - dy;
            int e2;
            int currentX = x0;
            int currentY = y0;

            while (true)
            {
                line.Enqueue(new GeoPoint(currentX, currentY));

                if (currentX == x1 && currentY == y1)
                {
                    break;
                }

                e2 = 2 * err;
                if (e2 > -1 * dy)
                {
                    err = err - dy;
                    currentX = currentX + sx;
                }

                if (e2 < dx)
                {
                    err = err + dx;
                    currentY = currentY + sy;
                }
            }

            return line.ToArray();
        }

        class GeoPointComparer : IComparer<GeoPoint>
        {
            public int Compare(GeoPoint x, GeoPoint y)
            {
                var deltay = x.Y - y.Y;
                if (deltay != 0) return deltay;
                return y.X - x.X;
            }
        }
    }
}
