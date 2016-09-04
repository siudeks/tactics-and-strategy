using Client.Domain;

namespace Client.Runtime
{
    /// <summary>
    /// Contains data related to pointer (e.g. mouse pointer) known location and 
    /// pointer command. It is possible by by definition all pointer commands 
    /// are unified independently of platform (touch has same abilities as mouse or pad)
    /// </summary>
    public struct PointerState
    {
        public GeoPoint Position;
    }
}
