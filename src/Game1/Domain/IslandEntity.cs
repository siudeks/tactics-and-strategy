namespace Game.Domain
{
    /// <summary>
    /// Represents an island definition.
    /// </summary>
    /// <remarks>
    /// Corners field represents ordered in clock-wise order collection of corners of the island.
    /// </remarks>
    public struct IslandEntity
    {
        public GeoPoint[] Corners;
    }
}
