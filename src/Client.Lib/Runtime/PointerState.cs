using Client.Domain;

namespace Client.Runtime
{
    /// <summary>
    /// Contains data related to pointer (e.g. mouse pointer) known location and 
    /// pointer command. 
    /// 
    /// To implement that abstract Pointer state we need to translate every platform-dependatn 
    /// input operation as well-known pointer command like 'select', 'zoom' etc.
    /// </summary>
    public struct PointerState
    {
        public GeoPoint Position;
    }
}
