using Game.Desktop.Native;
using Game.Domain;
using Game.Runtime;
using Game.View;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Reactive.Disposables;
using System.Reactive.Subjects;

namespace Game.Desktop
{
    /// <summary>
    /// This is the main type for your game.
    /// </summary>
    public class GameApp : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;

        // temporar variables to keep sample textures for demo purposes.
        private Texture2D terrainSprite;
        private IslandEntity island = IslandEntityGenerator.Random(new GeoPoint(20, 20));

        public GameApp()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
        }

        private readonly CompositeDisposable instanceDisposer = new CompositeDisposable();
        private BehaviorSubject<PointerState> pointerStateStream;

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            pointerStateStream = new BehaviorSubject<PointerState>(new PointerState());
            instanceDisposer.Add(pointerStateStream);

            // TODO: Add your initialization logic here
            Components.Add(new PointerObserver(pointerStateStream));

            base.Initialize();
        }

        // sprite used to mark a GeoPoint as selected.
        private Texture2D selectionSprite;

        Window window;

        SpriteFactory spriteFactory;
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);
            spriteFactory = new SpriteFactory(GraphicsDevice);

            terrainSprite = Content.Load<Texture2D>("Terrain");
            var appSprites = Content.Load<Texture2D>("DesertRatsSprites");

            selectionSprite = spriteFactory.CreateSelectorTexture().Texture2D;

            var waterTextures = new WaterTextures(terrainSprite);
            var cityTexture = new TextureHolder(terrainSprite, new Rectangle(7 * Config.SpriteSize, 9 * Config.SpriteSize, Config.SpriteSize, Config.SpriteSize));
            var groundTexture = new TextureHolder(terrainSprite, new Rectangle(0 * Config.SpriteSize, 0, Config.SpriteSize, Config.SpriteSize));
            var landUnitTexture = new TextureHolder(appSprites, new Rectangle(1 + 0 * Config.SpriteSize, 1 + 0, Config.SpriteSize, Config.SpriteSize));

            var spriteSize = new Rectangle(1, 1 + 1 + Config.SpriteSize, Config.SpriteSize, Config.SpriteSize);
            window = new Window(
                waterTextures,
                cityTexture,
                new DefaultStrategy(waterTextures.Sea),
                new CoastWithLandToTheNorthStrategy(waterTextures.CoastWithLandToTheNorth),
                new CoastWithLandToTheSouthStrategy(waterTextures.CoastWithLandToTheSouth),
                new GroundStrategy(groundTexture),
                new LandUnitStrategy(landUnitTexture),
                new CityStrategy(cityTexture)
                );
            window.AddIsland(island);
            window.AddCity(new CityEntity(20, 20));
            window.Include(new LandUnitEntity(21, 20));

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

            // get screen center
            var offsetx = graphics.GraphicsDevice.Viewport.Width / 2;
            var offsety = graphics.GraphicsDevice.Viewport.Height / 2;

            // display sample island
            {
                var points = window
                    .GetWindow(0, 0, 100, 100);

                foreach (var it in points)
                {
                    var position = new Vector2(it.GeoPoint.X * Config.SpriteSize, it.GeoPoint.Y * Config.SpriteSize);
                    spriteBatch.Draw(position, it.Texture);
                }
            }

            // drawing cursor Start
            var selectionPoint = pointerStateStream.Value.Position;
            var cameraSelectionPoint = new Point(selectionPoint.X * Config.SpriteSize, selectionPoint.Y * Config.SpriteSize);
            var selectionPosition = new Vector2(cameraSelectionPoint.X - 1, cameraSelectionPoint.Y - 1);
            spriteBatch.Draw(selectionSprite, selectionPosition, Color.White);

            spriteBatch.End();

            base.Draw(gameTime);
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);

            instanceDisposer.Dispose();
        }

        // non-testable method because Texture2D can't be created in unit tests.


    }
}
