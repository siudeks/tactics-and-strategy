package com.mygdx.game;

public class Game1 {

        private readonly CompositeDisposable instanceDisposer = new CompositeDisposable();
        private IContainer container;

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            var builder = new ContainerBuilder();
            // Scan an assembly for components
            builder.RegisterAssemblyModules(Assembly.GetAssembly(typeof(Game1)));

            builder.RegisterInstance(new SelectionSprite());
            container = builder.Build();

            var gameComponents = container.Resolve<IEnumerable<IGameComponent>>();
            foreach (var item in gameComponents)
                Components.Add(item);

            base.Initialize();
        }

        
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);

            var textureConsumer = container.Resolve<ITextureConsumer>();

            var terrainSprite = Content.Load<Texture2D>(@"Terrain");
            textureConsumer.OnLoaded(terrainSprite, TextureItem.TERRAIN);

            var appSprites = Content.Load<Texture2D>("DesertRatsSprites");
            textureConsumer.OnLoaded(appSprites, TextureItem.DESERT_RATES);

            textureConsumer.LoadFinished();



            var selectionTexture = CreateSelectorTexture(GraphicsDevice);

            var sprite = container.Resolve<SelectionSprite>();
            sprite.Texture = selectionTexture;

            var spriteSize = IntRectangle.of(1, 1 + 1 + Config.SpriteSize, Config.SpriteSize, Config.SpriteSize);

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

        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.White);

            spriteBatch.Begin();

            // drawing cursor Start
            var drawers = container.Resolve<IEnumerable<IBatchDrawer>>();
            foreach (var drawer in drawers)
                drawer.OnDraw(spriteBatch);

            spriteBatch.End();

            base.Draw(gameTime);
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);

            instanceDisposer.Dispose();
        }

        // non-testable method because Texture2D can't be created in unit tests.
        private static Texture2D CreateSelectorTexture(GraphicsDevice device)
        {
            var size = Config.SpriteSize + 2;
            var texture = device.CreateTexture(size, size);

            var colors = new Color[size * size];
            texture.GetData(colors);
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                {
                    var color = Color.Transparent;
                    var i = y * size + x;
                    if (x == 0 || x == size - 1) color = Color.Red;
                    if (y == 0 || y == size - 1) color = Color.Red;

                    colors[i] = color;

                }
            texture.SetData(colors);
            return texture;
        }

    }
}
