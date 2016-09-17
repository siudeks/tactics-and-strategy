using Microsoft.Xna.Framework;

namespace Client.View
{
    /// <summary>
    /// Camera fits to view where left top corner is (0, 0) and we need
    /// to convert coordinates to case when left bottom corner is (0, 0)
    /// </summary>
    public sealed class Camera
    {
        private int width;
        private int height;
        private readonly int tileSize;

        public Camera(int width, int height, int tileSize)
        {
            this.width = width;
            this.height = height;
            this.tileSize = tileSize;
        }

        /// <summary>
        /// Converts native point location (with (0, 0) as left top corner)
        /// to camera-aware location (with (0, 0) as left bottom corner)
        /// </summary>
        /// <param name="pointLocation">'in-game' point (means a pixel) location which need to be converted to real game pixel location.</param>
        /// <returns></returns>
        public Point View(Point pointLocation)
        {
            return new Point(pointLocation.X, height - pointLocation.Y - tileSize);
        }
    }
}
