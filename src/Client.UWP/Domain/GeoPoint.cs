using System.Diagnostics;

namespace Client.Domain
{
    /// <summary>
    /// Represents a square on 2D map.
    /// </summary>
    [DebuggerDisplay("{X}:{Y}")]
    public struct GeoPoint
    {
        /// <summary>
        /// X location of the middle of the square.
        /// </summary>
        public int X;

        /// <summary>
        /// Y location of the middle of the square.
        /// </summary>
        public int Y;

        /// <summary>
        /// Creates a new instance of <see cref="GeoPoint"/>
        /// </summary>
        /// <param name="x">X coordinate.</param>
        /// <param name="y">Y coordinate.</param>
        public GeoPoint(int x, int y)
        {
            X = x;
            Y = y;
        }

        public GeoPoint Top() => new GeoPoint(X, Y + 1);
        public GeoPoint Down() => new GeoPoint(X, Y - 1);
        public GeoPoint Left() => new GeoPoint(X - 1, Y);
        public GeoPoint Right() => new GeoPoint(X + 1, Y);
        public GeoPoint TopLeft() => new GeoPoint(X - 1, Y + 1);
        public GeoPoint TopRight() => new GeoPoint(X + 1, Y + 1);
        public GeoPoint DownLeft() => new GeoPoint(X - 1, Y - 1);
        public GeoPoint DownRight() => new GeoPoint(X + 1, Y - 1);
    }
}
