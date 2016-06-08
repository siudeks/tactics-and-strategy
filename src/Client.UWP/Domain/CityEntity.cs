namespace Client.Domain
{
    public sealed class CityEntity
    {
        public readonly int X;
        public readonly int Y;
        public CityEntity(int x, int y)
        {
            X = x;
            Y = y;
        }
    }
}
