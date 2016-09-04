using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;

namespace Client.Runtime
{
    /// <summary>
    /// Allows to mark a GeoPoint as selected.
    /// </summary>
    public sealed class PointerDrawer
    {
        private readonly IObservable<PointerState> pointerStream;
        public PointerDrawer(IObservable<PointerState> pointerStream)
        {
            this.pointerStream = pointerStream;
        }
    }
}
