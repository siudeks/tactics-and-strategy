using System;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;

namespace Client.Runtime
{
    /// <summary>
    /// Observes currently selected GeoPoint and informs when and what is selected.
    /// </summary>
    public sealed class PointerObserver : IUpdateable, IGameComponent
    {
        public bool Enabled { get; } = true;
        public int UpdateOrder { get; } = 0;


        public event EventHandler<EventArgs> EnabledChanged;
        public event EventHandler<EventArgs> UpdateOrderChanged;

        private IObserver<PointerState> pointerStateStream;
        public PointerObserver(IObserver<PointerState> pointerStateStream)
        {
            this.pointerStateStream = pointerStateStream;
        }

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
            var state = new PointerState();
            state.Position = new Domain.GeoPoint();
            pointerStateStream.OnNext(state);
        }
    }
}
