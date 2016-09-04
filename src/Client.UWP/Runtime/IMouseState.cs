
namespace Client.Runtime
{
    /// <summary>
    /// REpresents last known mouse position.
    /// </summary>
    public interface IMouseState
    {
        int X { get; }
        int Y { get; }
    }
}
