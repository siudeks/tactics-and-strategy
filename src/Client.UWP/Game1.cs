﻿using Client.Domain;
using Client.View;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;
using System.Linq;

namespace Client.UWP
{
    /// <summary>
    /// This is the main type for your game.
    /// </summary>
    public class Game1 : Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;

        // temporar variables to keep sample textures for demo purposes.
        private Texture2D terrainSprite;
        private IslandEntity island = IslandEntityGenerator.Random(new GeoPoint(20, 20));

        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here

            base.Initialize();
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);

            terrainSprite = Content.Load<Texture2D>(@"Terrain");
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// game-specific content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            // TODO: Add your update logic here

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.White);

            spriteBatch.Begin();

            // get screen center
            var offsetx = graphics.GraphicsDevice.Viewport.Width / 2;
            var offsety = graphics.GraphicsDevice.Viewport.Height / 2;

            // display sample island
            {
                var waterTextures = new WaterTextures(terrainSprite);

                var spriteSize = new Rectangle(1, 1 + 1 + 32, 32, 32);
                var window = new Window(
                    waterTextures,
                    new TextureHolder(terrainSprite, new Rectangle(0 * 32, 0, 32, 32)),
                    new TextureHolder(terrainSprite, new Rectangle(7 * 32, 9 * 32, 32, 32)));
                window.AddIsland(island);
                window.AddCity(new CityEntity(20, 20));

                var points = window
                    .GetWindow(0, 0, 100, 100);

                var displayHeigh = GraphicsDevice.Viewport.Height;
                foreach (var it in points)
                {
                    var position = new Vector2(it.GeoPoint.X * 32, displayHeigh - it.GeoPoint.Y * 32);
                    spriteBatch.Draw(position, it.Texture);
                }
            }

            spriteBatch.End();

            base.Draw(gameTime);
        }
    }
}
