using System;
using System.Diagnostics;

namespace Game.Runtime
{
    /// <summary>
    /// Allows to mark a GeoPoint as selected.
    /// 
    /// TODO Move to this class logic about marking GeoPoint 'selected' on UI.
    /// </summary>
    public sealed class PointerDrawer
    {
        private readonly IObservable<PointerState> pointerStream;
        public PointerDrawer(IObservable<PointerState> pointerStream)
        {
            this.pointerStream = pointerStream;
            Debug.Print("aaaaaaaaaaaaaaaa");
        }
    }
}
