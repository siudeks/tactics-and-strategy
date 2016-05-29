namespace Client.Domain
{
    /// <summary>
    /// Represents an island definition.
    /// </summary>
    /// <remarks>
    /// Corners field represents ordered collection of corners of the island.
    /// </remarks>
    public struct IslandEntity
    {
        public GeoPoint[] Corners;
    }
}
