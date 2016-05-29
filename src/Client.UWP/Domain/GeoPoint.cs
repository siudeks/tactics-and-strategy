namespace Client.Domain
{
    /// <summary>
    /// Represents a square on 2D map.
    /// </summary>
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
    }
}
