using Client.Domain;
using System.Diagnostics;

namespace Client.Runtime
{
    /// <summary>
    /// Contains data related to pointer (e.g. mouse pointer on desktop app) known location and 
    /// pointer command. 
    /// 
    /// To implement that abstract Pointer state we need to translate every platform-dependatn 
    /// input operation as well-known pointer command like 'select', 'zoom' etc.
    /// </summary>
    [DebuggerDisplay("Position: X:{Position.X} Y:{Position.Y}")]
    public record PointerState
    {
        public GeoPoint Position { get; init; }
    }
}
