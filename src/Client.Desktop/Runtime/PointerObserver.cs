using Client.Domain;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;
using System;

namespace Client.Runtime
{
    /// <summary>
    /// Observes which GeoPoint is currently selected 
    /// and streams events about the selection.
    /// </summary>
    public sealed class PointerObserver : IUpdateable, IGameComponent
    {
        public bool Enabled { get; } = true;
        public int UpdateOrder { get; } = 0;


        public event EventHandler<EventArgs> EnabledChanged;
        public event EventHandler<EventArgs> UpdateOrderChanged;

        private IObserver<PointerState> pointerStateStream;

        /// <summary>
        /// Creates a new instance of <see cref="PointerObserver"/>.
        /// </summary>
        /// <param name="pointerStateStream">Shared application listener about current pointer state.</param>
        public PointerObserver(IObserver<PointerState> pointerStateStream)
        {
            this.pointerStateStream = pointerStateStream ;
        }

        /// <summary>
        /// Post-construction method.
        /// 
        /// Currentluy not used, but required by implemented interface.
        /// </summary>
        public void Initialize()
        {
        }

        /// <summary>
        /// Method used in runtime by MonoGame framework.
        /// 
        /// For test use <see cref="PointerObserver.Update"/> Update method with more parameters.
        /// </summary>
        /// <param name="gameTime"></param>
        void IUpdateable.Update(GameTime gameTime)
        {
            Update(gameTime, Mouse.GetState().Position);
        }

        public void Update(GameTime gameTime, Point mousePosition)
        {
            var x = mousePosition.X / Config.SpriteSize;
            var y = mousePosition.Y / Config.SpriteSize;

            var state = new PointerState()
            {
                Position = new GeoPoint { X = x, Y = y }
            };

            pointerStateStream.OnNext(state);
        }
    }
}
