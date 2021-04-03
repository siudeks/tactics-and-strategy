package com.mygdx.game.domain;

import java.util.Random;

import io.vavr.collection.List;

/** Holds methods related to generating an island. */
public class IslandEntityGenerator {
    /// <summary>
    /// Generates random island where some defined points need to be included in island as a 'centre'. 
    /// </summary>
    /// <param name="center">Centre of the island.</param>
    /// <returns>Definition of generated island.</returns>
    public static IslandEntity Random(GeoPoint... center) {
        var random = new Random();

        var corners = List.<GeoPoint>empty();
        // Start from negative angle to randomize more island generation. We don't know
        // what would be the first angle because it starts from -100 with random incrementation.
        // Only angles between [0; 360) will be included in map generation.
        int currentAngle = -100;
        while (true) {
            var actual = random.nextInt(30) + 30;
            var length = random.nextInt(10) + 5;

            currentAngle += actual;
            if (currentAngle >= 360) break;
            if (currentAngle < 0) continue;

            var x = (int)(Math.cos(currentAngle * Math.PI / 360 * 2) * length);
            var y = (int)(Math.sin(currentAngle * Math.PI / 360 * 2) * length);

            var candidate = (GeoPoint) null;
            var distance = 0d;
            for (int j = 0; j < center.length; j++) {
                var point = new GeoPoint(x + center[j].X, y + center[j].Y);
                var distanceTotal = 0d;
                for (int i = 0; i < center.length; i++) {
                    var testedCenter = center[i];
                    var testedDistance = Math.sqrt(Math.pow(point.X - testedCenter.X, 2) + Math.pow(point.Y - testedCenter.Y, 2));
                    distanceTotal += testedDistance;
                }
                if (distanceTotal > distance) {
                    distance = distanceTotal;
                    candidate = point;
                }
            }
            corners = corners.append(candidate);
        }

        return new IslandEntity(corners.toJavaArray(GeoPoint[]::new));
    }
}
