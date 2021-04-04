package com.mygdx.game.domain;

import com.mygdx.game.view.Vector2;

import io.vavr.collection.Queue;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Iterator;
import io.vavr.Tuple2;
import io.vavr.Tuple;

import java.util.Comparator;

public class IslandEntityExtensions {
    
    /** Converts list of island borders to the list of all island tiles. */
    public static GeoPoint[] GeneratePoints(IslandEntity entries) {
        return FillPolygon(entries.Corners())
            .map(o -> new GeoPoint(o.X(), o.Y()))
            .toJavaArray(GeoPoint[]::new);
    }

    // http://alienryderflex.com/polygon_fill/
    private static Seq<Vector2> FillPolygon(GeoPoint[] corners) {
        // convert all verticles to edges.
        var edges = List.<List<GeoPoint>>empty();
        var cornersCount = corners.length;
        for (int i = 0; i < cornersCount; i++) {
            var next = i + 1;
            if (next == cornersCount) next = 0;

            edges = edges.append(AsLine(corners[i].X, corners[i].Y, corners[next].X, corners[next].Y));
        }

        // prepare all points of edges for horizontal linescan
        var sortedEdges = edges
            .flatMap(it -> it)
            // TODO .sort(it -> it -> it, new GeoPointComparer())
            .toJavaArray(GeoPoint[]::new);

        // convert edges to horizontal lines which fill the shape.
        var lineStart = sortedEdges[0];
        var lineEnd = sortedEdges[0];
        var lines = Queue.<Tuple2<GeoPoint, GeoPoint>>empty();
        for (int i = 1; i < sortedEdges.length; i++) {
            // find the most right element in line and continue
            if (sortedEdges[i].Y == lineStart.Y)
            {
                lineEnd = sortedEdges[i];
                continue;
            }

            // hold just finished line and create new one
            lines = lines.enqueue(Tuple.of(lineStart, lineEnd));
            lineStart = sortedEdges[i];
            lineEnd = sortedEdges[i];
        }
        lines = lines.enqueue(Tuple.of(lineStart, lineEnd));

        // now in lines we have all horizontal lines, so let's fill the polygon
        var points = List.<Vector2>empty();
        for (var item : lines) {
            var liney = item._1.Y;

            //horizontal line contains at least initial point
            points = points.append(new Vector2(item._1.X, liney));

            // if initial point is the same as end point, need to go to the next line.
            if (item._1.X == item._2.X) continue;

            for (int x = item._1.X + 1; x < item._2.X; x++)
                points = points.append(new Vector2(x, liney));

            points = points.append(new Vector2(item._2.X, liney));
        }

        return points;

    }

    // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    // https://github.com/fragkakis/bresenham/blob/master/src/main/java/org/fragkakis/Bresenham.java    
    private static List<GeoPoint> AsLine(int x0, int y0, int x1, int y1)
    {
        var line = List.<GeoPoint>empty();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;
        int currentX = x0;
        int currentY = y0;

        while (true)
        {
            line = line.append(new GeoPoint(currentX, currentY));

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

        return line;
    }

    /// <summary>
    /// Allows to sort points from left top corner to right down row by row.
    /// 
    /// Rember in XNA axis are directed as above:
    /// +---> X
    /// | 1   2
    /// |  3
    /// |     4
    /// v Y
    /// 
    /// So points 1 to 4 will be returned as 1,2,3,4
    /// </summary>
    class GeoPointComparer implements Comparator<GeoPoint> {
        public int compare(GeoPoint first, GeoPoint second) {
            var deltay = first.Y - second.Y;
            if (deltay != 0) return deltay;
            return first.X - second.X;
        }
    }
}
