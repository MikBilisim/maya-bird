package com.tambai.mayabird;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Amir
 * @since September 2014
 */

public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener
{
    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------
    public static final int CAMERA_WIDTH = 480;
    public static final int CAMERA_HEIGHT = 800;
    private Camera camera;

    private Scene gameScene;
    private HUD gameHud;

    // layers
    private Entity splashSceneLayer;
    private Entity mainMenuLayer;
    private Entity gameScreenLayer;

    // graphics
    private BuildableBitmapTextureAtlas splashTextureAtlas;
    private BuildableBitmapTextureAtlas menuAndGameTextureAtlas;
    private ITextureRegion splashBgRegion;
    private ITextureRegion tambaiGamesLogoRegion;
    private ITextureRegion dcsisRegion;
    private ITextureRegion mayaBirdBgRegion;
    private ITextureRegion mayaBirdMenuRegion;
    private ITextureRegion groundRegion;
    private ITextureRegion surfaceRegion;
    private ITextureRegion playButtonRegion;
    private TiledTextureRegion redFlappyBirdRegion;
    private TiledTextureRegion yellowFlappyBirdRegion;
    private ITextureRegion tapRegion;
    private TiledTextureRegion mayaBirdRegion;

    private Sprite splashBgSprite;
    private Sprite tambaiGamesLogoSprite;
    private Sprite dcsisSprite;
    private Sprite mayaBirdBgSprite;
    private Sprite mayaBirdMenuSprite;
    private Sprite groundSprite;
    private Sprite surfaceSprite;
    private Sprite playButtonSprite;
    private AnimatedSprite flappyBirdSprite;
    private Sprite tapSprite;
    private AnimatedSprite mayaBirdSprite;

    // fonts
    private Font loadingFont;
    private Font scoreFont;
    private Font gameOverFont;
    private Font resultFont;

    private Text loadingText;
    private Text scoreText;
    private Text gameOverText;
    private Text resultText;

    // sounds
    private Sound clickSound;
    private Sound flapSound;
    private Sound badingSound;
    private Sound smackSound;

    // music
    private Music gameBgMusic;

    // in-game
    private AutoParallaxBackground autoParallaxBackground;
    private float gameParallaxChangePerSecond = 10f;
    private float surfaceParallaxFactor = -10f;

    private IUpdateHandler physicsWorldUpdate;
    private PhysicsWorld gamePhysicsWorld;
    private PhysicsConnector physicsConnector;
    private FixtureDef groundFixtureDef;
    private FixtureDef mayaBirdFixtureDef;
    private Body groundBody;
    private Body mayaBirdBody;
    private float vX = 0f;
    private float vY = 0f;

    private LinkedList<Sprite> flappyBirdSpawner;
    private LinkedList<Sprite> flappyBirdDetector;
    private TimerHandler flappyBirdSpawnTimerHandler;
    private IUpdateHandler flappyBirdDetectUpdateHandler;
    private float flappyBirdSpawnDelay = 3f;
    private int flappyX;
    private int flappyY;

    private int best;
    private int score;
    private int tapCount;
    private boolean isGameStart;
    private boolean isPlaying;
    private boolean isGameOver;

    private final int RED_BIRD_SCORE = -1;
    private final int YELLOW_BIRD_SCORE = 1;

    //-----------------------------------------
    // UPDATE HANDLER
    //-----------------------------------------

    // updates the game every time the scene refreshes
    private void gameUpdateHandler()
    {
        IUpdateHandler gameUpdate = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                if (!isGameStart && (mayaBirdSprite.getX() == camera.getWidth() * 0.30f))
                {
                    // starts the game
                    isGameStart = true;
                    gameScene.unregisterUpdateHandler(this);
                }
            }

            @Override
            public void reset()
            {
            }
        };

        gameScene.registerUpdateHandler(gameUpdate);
    }

    // removes all unnecessary updates
    private void removeUpdates()
    {
        mayaBirdBgSprite.setIgnoreUpdate(true);
        groundSprite.setIgnoreUpdate(true);
        surfaceSprite.setIgnoreUpdate(true);
        tapSprite.setIgnoreUpdate(true);

        scoreText.setIgnoreUpdate(true);
    }

    //-----------------------------------------
    // GAME ACTIVITY METHODS
    //-----------------------------------------
    @Override
    public EngineOptions onCreateEngineOptions()
    {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        RatioResolutionPolicy canvas = new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true,
                ScreenOrientation.PORTRAIT_FIXED, canvas, camera);

        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getAudioOptions().setNeedsSound(true).getSoundOptions().setMaxSimultaneousStreams(3);
        engineOptions.getAudioOptions().setNeedsMusic(true);

        return engineOptions;
    }

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions)
    {
        return new FixedStepEngine(pEngineOptions, 60);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
    {
        // graphics
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        splashTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 512, TextureOptions.BILINEAR);

        splashBgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
                this.getAssets(), "splash_bg.png");
        tambaiGamesLogoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
                this.getAssets(), "tambai_games_logo.png");
        dcsisRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
                this.getAssets(), "dcsis.png");

        try
        {
            splashTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 1));
            splashTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }

        // fonts
        FontFactory.setAssetBasePath("fonts/");

        ITexture loadingFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        loadingFont = FontFactory.createStrokeFromAsset(this.getFontManager(), loadingFontTexture,
                this.getAssets(), "game_font.TTF", camera.getWidth() * 0.09375f, true, Color.WHITE_ABGR_PACKED_INT,
                camera.getWidth() * 0.003125f, Color.BLACK_ABGR_PACKED_INT);

        loadingFont.load();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
    {
        // creating and setting the game scene
        gameScene = new Scene();
        gameScene.getBackground().setColor(Color.WHITE);

        // creating and attaching layers
        splashSceneLayer = new Entity();
        mainMenuLayer = new Entity();
        gameScreenLayer = new Entity();

        gameScene.attachChild(splashSceneLayer);
        gameScene.attachChild(mainMenuLayer);
        gameScene.attachChild(gameScreenLayer);

        // setting layer visibility
        splashSceneLayer.setVisible(true);
        mainMenuLayer.setVisible(false);
        gameScreenLayer.setVisible(false);

        // sets the game scene touch listener to the implemented interface
        gameScene.setOnSceneTouchListener(this);

        pOnCreateSceneCallback.onCreateSceneFinished(gameScene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
    {
        // load the splash scene
        loadSplashScene();

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int pKeyCode, KeyEvent pEvent)
    {
        if (pKeyCode == KeyEvent.KEYCODE_BACK)
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
    {
        if (isGameStart && !isGameOver)
        {
            if (pSceneTouchEvent.isActionDown())
            {
                tapCount++;

                if (tapCount == 1)
                {
                    mayaBirdSprite.setRotation(-15f);
                    destroyEntity(tapSprite);
                    createPhysics();
                    gameScene.registerUpdateHandler(flappyBirdSpawnTimerHandler);
                    gameScene.registerUpdateHandler(flappyBirdDetectUpdateHandler);
                }

                if (mayaBirdSprite.getY() < camera.getHeight())
                {
                    flyMayaBird(mayaBirdSprite);
                    return true;
                }
            }
            else if (pSceneTouchEvent.isActionUp())
            {
                if (isPlaying)
                {
                    mayaBirdSprite.clearEntityModifiers();
                    RotationModifier rotMod = new RotationModifier(1f, mayaBirdSprite.getRotation(), 25f);
                    mayaBirdSprite.registerEntityModifier(rotMod);

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onResumeGame()
    {
        if (gameBgMusic != null && !gameBgMusic.isPlaying() && !isPlaying)
        {
            gameBgMusic.play();
        }

        super.onResumeGame();
    }

    @Override
    public void onPauseGame()
    {
        if (gameBgMusic != null && gameBgMusic.isPlaying())
        {
            gameBgMusic.pause();
        }

        super.onPauseGame();
    }

    //-----------------------------------------
    // GAME LOGIC METHODS
    //-----------------------------------------

    // loads both the menu and game resources
    private void loadMenuAndGameResources()
    {
        // graphics
        menuAndGameTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

        mayaBirdBgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "maya_bird_bg.png");
        mayaBirdMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "maya_bird_menu.png");
        groundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "ground.png");
        surfaceRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "surface.png");
        playButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "play_button.png");
        redFlappyBirdRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "red_flappy_bird.png", 3, 1);
        yellowFlappyBirdRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "yellow_flappy_bird.png", 3, 1);
        tapRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "tap.png");
        mayaBirdRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAndGameTextureAtlas,
                this.getAssets(), "maya_bird.png", 10, 1);

        try
        {
            menuAndGameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 1));
            menuAndGameTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }

        // fonts
        ITexture scoreFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        scoreFont = FontFactory.createStrokeFromAsset(this.getFontManager(), scoreFontTexture,
                this.getAssets(), "game_font.TTF", camera.getWidth() * 0.15625f, true, Color.WHITE_ABGR_PACKED_INT,
                camera.getWidth() * 0.01f, Color.BLACK_ABGR_PACKED_INT);

        scoreFont.load();

        ITexture gameOverFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        gameOverFont = FontFactory.createStrokeFromAsset(this.getFontManager(), gameOverFontTexture,
                this.getAssets(), "game_font.TTF", camera.getWidth() * 0.15f, true, Color.WHITE_ABGR_PACKED_INT,
                camera.getWidth() * 0.01f, Color.BLACK_ARGB_PACKED_INT);

        gameOverFont.load();

        ITexture resultFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        resultFont = FontFactory.createStrokeFromAsset(this.getFontManager(), resultFontTexture,
                this.getAssets(), "game_font.TTF", camera.getWidth() * 0.1f, true, Color.WHITE_ABGR_PACKED_INT,
                camera.getWidth() * 0.008f, Color.BLACK_ARGB_PACKED_INT);

        resultFont.load();

        // sounds
        SoundFactory.setAssetBasePath("sfx/");

        try
        {
            clickSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "click.mp3");
            flapSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "flap.wav");
            badingSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "bading.wav");
            smackSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "smack.wav");
        }
        catch (IOException e)
        {
            Debug.e(e);
        }

        // music
        MusicFactory.setAssetBasePath("mfx/");

        try
        {
            gameBgMusic = MusicFactory.createMusicFromAsset(this.getMusicManager(), this, "game_bg.mp3");
        }
        catch (IOException e)
        {
            Debug.e(e);
        }
    }

    // loads the splash scene layer and its objects
    private void loadSplashScene()
    {
        // creating and attaching splash bg sprite
        splashBgSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                camera.getWidth(), camera.getHeight(), splashBgRegion,
                this.getVertexBufferObjectManager());

        splashSceneLayer.attachChild(splashBgSprite);

        // creating and attaching tambai games logo sprite
        gameScene.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                tambaiGamesLogoSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                        camera.getWidth(), camera.getHeight(), tambaiGamesLogoRegion,
                        MainActivity.this.getVertexBufferObjectManager());

                splashSceneLayer.attachChild(tambaiGamesLogoSprite);
                fadeInEntity(tambaiGamesLogoSprite, 0.5f);
            }
        }));

        // fading out the tambai games logo sprite
        gameScene.registerUpdateHandler(new TimerHandler(4f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                fadeOutEntity(tambaiGamesLogoSprite, 0.5f);
            }
        }));

        // creating and attaching dcsis sprite
        gameScene.registerUpdateHandler(new TimerHandler(5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                dcsisSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                        camera.getWidth(), camera.getHeight(), dcsisRegion,
                        MainActivity.this.getVertexBufferObjectManager());

                splashSceneLayer.attachChild(dcsisSprite);
                fadeInEntity(dcsisSprite, 0.5f);
            }
        }));

        // creating and attaching loading text
        gameScene.registerUpdateHandler(new TimerHandler(7f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                loadingText = new Text(camera.getWidth() / 2, camera.getHeight() / 4,
                        loadingFont, "loading...", MainActivity.this.getVertexBufferObjectManager());

                splashSceneLayer.attachChild(loadingText);

                loadMenuAndGameResources();
            }
        }));

        // fading out the splash scene layer entity and navigating to main menu layer
        gameScene.registerUpdateHandler(new TimerHandler(10f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                fadeOutEntity(dcsisSprite, 0.5f);
                fadeOutEntity(loadingText, 0.5f);
                fadeOutEntity(splashSceneLayer, 0.5f);
                splashSceneLayer.setVisible(false);

                loadMainMenu();
                mainMenuLayer.setVisible(true);

                fadeInEntity(mayaBirdBgSprite, 0.5f);
                fadeInEntity(mayaBirdMenuSprite, 0.5f);
                fadeInEntity(playButtonSprite, 0.5f);
                fadeInEntity(groundSprite, 0.5f);
                fadeInEntity(surfaceSprite, 0.5f);
                fadeInEntity(mainMenuLayer, 0.5f);
            }
        }));

        // destroying entities of splash scene layer and itself
        gameScene.registerUpdateHandler(new TimerHandler(12f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                destroyEntity(splashBgSprite);
                destroyEntity(tambaiGamesLogoSprite);
                destroyEntity(dcsisSprite);
                destroyEntity(loadingText);
                destroyEntity(splashSceneLayer);
                splashTextureAtlas.unload();
                loadingFont.unload();
            }
        }));
    }

    // loads the main menu layer and its objects
    private void loadMainMenu()
    {
        // creating and attaching maya bird bg sprite
        if (mayaBirdBgSprite == null)
        {
            mayaBirdBgSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                    camera.getWidth(), camera.getHeight(), mayaBirdBgRegion,
                    this.getVertexBufferObjectManager());
        }

        // creating and attaching ground sprite
        if (groundSprite == null)
        {
            groundSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() * 0.11667f,
                    camera.getWidth(), camera.getHeight() * 0.23333f, groundRegion,
                    this.getVertexBufferObjectManager());
        }

        // creating and attaching surface menu sprite
        if (surfaceSprite == null)
        {
            surfaceSprite = new Sprite(camera.getWidth() / 2, groundSprite.getHeight(),
                    camera.getWidth(), camera.getHeight() * 0.04167f, surfaceRegion,
                    this.getVertexBufferObjectManager());
        }

        // creating the parallax background and attaching the parallax entities
        if (autoParallaxBackground == null)
        {
            autoParallaxBackground = new AutoParallaxBackground(0f, 0f, 0f, gameParallaxChangePerSecond);
            autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0f, mayaBirdBgSprite));
            autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0f, groundSprite));
            autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(surfaceParallaxFactor, surfaceSprite));
            gameScene.setBackground(autoParallaxBackground);
        }

        // creating and attaching maya bird menu sprite
        mayaBirdMenuSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                camera.getWidth(), camera.getHeight(), mayaBirdMenuRegion,
                this.getVertexBufferObjectManager());

        mainMenuLayer.attachChild(mayaBirdMenuSprite);

        // creating and attaching play button sprite
        playButtonSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                camera.getWidth() * 0.3625f, camera.getHeight() * 0.14375f, playButtonRegion,
                this.getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if (pSceneTouchEvent.isActionDown())
                {
                    clickSound.play();
                    playButtonSprite.setScale(0.9f);
                }
                else if (pSceneTouchEvent.isActionUp())
                {
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            gameBgMusic.stop();
                            loadGameScreen();
                            gameScreenLayer.setVisible(true);

                            mainMenuLayer.setVisible(false);
                            gameScene.unregisterTouchArea(playButtonSprite);

                            destroyEntity(mayaBirdMenuSprite);
                            destroyEntity(playButtonSprite);
                            destroyEntity(mainMenuLayer);
                        }
                    });
                }
                else if (pSceneTouchEvent.isActionMove())
                {
                    if (pSceneTouchEvent.getX() > playButtonSprite.getWidth()
                            || pSceneTouchEvent.getY() > playButtonSprite.getHeight()
                            || pSceneTouchEvent.getX() < 0
                            || pSceneTouchEvent.getY() < 0)
                    {
                        playButtonSprite.setScale(1f);
                    }
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        // registering the play button sprite
        gameScene.registerTouchArea(playButtonSprite);

        mainMenuLayer.attachChild(playButtonSprite);

        // playing the game background music
        gameBgMusic.play();
        gameBgMusic.setLooping(true);
    }

    // loads the game screen layer and its objects
    private void loadGameScreen()
    {
        // initializing variables
        best = getBestScore();
        score = 0;
        tapCount = 0;
        isGameStart = false;
        isPlaying = true;
        isGameOver = false;

        flappyBirdSpawner = new LinkedList<Sprite>();
        flappyBirdDetector = new LinkedList<Sprite>();

        // creating and setting the camera hud
        if (gameHud == null)
        {
            gameHud = new HUD();
            camera.setHUD(gameHud);
        }

        // creating and attaching score text
        scoreText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.85f,
                scoreFont, "0123456789", this.getVertexBufferObjectManager());
        scoreText.setText(String.valueOf(score));

        gameHud.attachChild(scoreText);

        // creating and attaching maya bird sprite
        mayaBirdSprite = new AnimatedSprite(-mayaBirdRegion.getWidth(), camera.getHeight() / 2,
                camera.getWidth() * 0.10625f, camera.getHeight() * 0.05416f,
                mayaBirdRegion, this.getVertexBufferObjectManager());

        gameScreenLayer.attachChild(mayaBirdSprite);

        // animating maya bird sprite
        mayaBirdSprite.animate(new long[]{100, 100, 100, 100, 100, 100, 100, 100});

        // moves the maya bird sprite
        MoveXModifier movXMod = new MoveXModifier(2f, -mayaBirdSprite.getWidth(), camera.getWidth() * 0.30f);
        mayaBirdSprite.registerEntityModifier(movXMod);

        // creating and attaching tap sprite
        tapSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                camera.getWidth() * 0.35625f, camera.getHeight() * 0.20833f,
                tapRegion, this.getVertexBufferObjectManager());

        gameHud.attachChild(tapSprite);

        // calls the time handler for spwaning flappy birds
        createFlappyBirdSpawnTimeHandler();

        // calls the update handler of the game
        gameUpdateHandler();

        // removes unnecessary updates on entities
        removeUpdates();
    }

    //-----------------------------------------
    // GAME FUNCTION METHODS
    //-----------------------------------------

    // calls all the game over functions
    private void gameOver()
    {
        isPlaying = false;
        smackSound.play();
        mayaBirdSprite.stopAnimation(9);
        mayaBirdSprite.setRotation(90f);
        autoParallaxBackground.setParallaxChangePerSecond(0f);

        if (score > best)
        {
            best = score;
            setBestScore(best);
        }

        // creating and attaching game over text
        gameOverText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.8f,
                gameOverFont, "Game Over!", MainActivity.this.getVertexBufferObjectManager());

        gameHud.attachChild(gameOverText);

        // creating and attaching result text
        resultText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.65f,
                resultFont, "Score: " + String.valueOf(score) + "\nBest: " + String.valueOf(best),
                MainActivity.this.getVertexBufferObjectManager());

        gameHud.attachChild(resultText);

        // calls game over functions
        gameScene.unregisterUpdateHandler(flappyBirdSpawnTimerHandler);
        destroyEntity(scoreText);
        showPlayButton();
    }

    // creates the physics needed by the game to function
    private void createPhysics()
    {
        // creating physics world gravity and setting its contact listener
        gamePhysicsWorld = new PhysicsWorld(new Vector2(0, -SensorManager.GRAVITY_EARTH * 3), false);
        gamePhysicsWorld.setContactListener(createContactListener());

        // creating fixture definition and body of ground sprite
        groundFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        groundBody = PhysicsFactory.createBoxBody(gamePhysicsWorld,
                groundSprite, BodyType.StaticBody, groundFixtureDef);
        groundBody.setUserData("ground");
        groundSprite.setUserData(groundBody);

        // creating fixture definition and body of maya bird sprite
        mayaBirdFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        mayaBirdBody = PhysicsFactory.createCircleBody(gamePhysicsWorld,
                mayaBirdSprite, BodyType.DynamicBody, mayaBirdFixtureDef);
        mayaBirdBody.setUserData("maya_bird");
        mayaBirdSprite.setUserData(mayaBirdBody);

        // creating physics connector and registering the physics world
        physicsConnector = new PhysicsConnector(mayaBirdSprite, mayaBirdBody, true, false);
        gamePhysicsWorld.registerPhysicsConnector(physicsConnector);

        // creating update handler for the physics world and updates it on every scene update
        physicsWorldUpdate = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                gamePhysicsWorld.onUpdate(pSecondsElapsed);
            }

            @Override
            public void reset()
            {
            }
        };

        // registering the update handler
        gameScene.registerUpdateHandler(physicsWorldUpdate);
    }

    // destroys the physics created in the game
    private void destroyPhysics()
    {
        gamePhysicsWorld.setContactListener(null);
        gamePhysicsWorld.unregisterPhysicsConnector(physicsConnector);
        gamePhysicsWorld.clearPhysicsConnectors();
        gameScene.unregisterUpdateHandler(physicsWorldUpdate);
        gamePhysicsWorld = null;
        groundFixtureDef = null;
        groundBody = null;
        mayaBirdFixtureDef = null;
        mayaBirdBody = null;
        physicsConnector = null;
    }

    // makes the maya bird fly
    private void flyMayaBird(final Sprite pMayaBird)
    {
        flapSound.play();
        mayaBirdSprite.clearEntityModifiers();
        RotationModifier rotMod = new RotationModifier(0.3f, mayaBirdSprite.getRotation(), -45f);
        mayaBirdSprite.registerEntityModifier(rotMod);
        final Body pMayaBirdBody = (Body) pMayaBird.getUserData();

        vY = camera.getHeight() * 0.0125f;
        final Vector2 velocity = Vector2Pool.obtain(vX, vY);
        pMayaBirdBody.setLinearVelocity(velocity);
        Vector2Pool.recycle(velocity);
    }

    // contact listener that checks the collision of physics objects in the game like  the ground and maya bird
    private ContactListener createContactListener()
    {
        ContactListener contactListener = new ContactListener()
        {
            // create the two bodies that will begin a contact with each one

            @Override
            public void beginContact(Contact contact)
            {
                final Fixture fixtureA = contact.getFixtureA();
                final Body bodyA = fixtureA.getBody();
                final String userDataA = (String) bodyA.getUserData();

                final Fixture fixtureB = contact.getFixtureB();
                final Body bodyB = fixtureB.getBody();
                final String userDataB = (String) bodyB.getUserData();

                if (("maya_bird".equals(userDataA)) && ("ground".equals(userDataB))
                        || ("ground".equals(userDataA)) && ("maya_bird".equals(userDataB)))
                {
                    if (!isGameOver)
                    {
                        isGameOver = true;

                        if (isGameOver)
                        {
                            gameOver();
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {
            }
        };

        return contactListener;
    }

    // shows the play button during score preview on game over
    private void showPlayButton()
    {
        // creating and attaching play button sprite
        playButtonSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                camera.getWidth() * 0.3625f, camera.getHeight() * 0.14375f, playButtonRegion,
                this.getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if (pSceneTouchEvent.isActionDown())
                {
                    clickSound.play();
                    playButtonSprite.setScale(0.9f);
                }
                else if (pSceneTouchEvent.isActionUp())
                {
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Iterator<Sprite> flappyBirdList = flappyBirdDetector.iterator();
                            Sprite flappyBird;

                            while (flappyBirdList.hasNext())
                            {
                                flappyBird = flappyBirdList.next();

                                // destroys flappy bird and breaks process
                                destroyEntity(flappyBird);
                                flappyBirdList.remove();

                                break;
                            }

                            destroyEntity(mayaBirdSprite);
                            destroyEntity(gameOverText);
                            destroyEntity(resultText);
                            destroyEntity(playButtonSprite);
                            destroyPhysics();
                            autoParallaxBackground.setParallaxChangePerSecond(10f);
                            loadGameScreen();
                            gameScene.unregisterTouchArea(playButtonSprite);
                        }
                    });
                }
                else if (pSceneTouchEvent.isActionMove())
                {
                    if (pSceneTouchEvent.getX() > playButtonSprite.getWidth()
                            || pSceneTouchEvent.getY() > playButtonSprite.getHeight()
                            || pSceneTouchEvent.getX() < 0
                            || pSceneTouchEvent.getY() < 0)
                    {
                        playButtonSprite.setScale(1f);
                    }
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        // registering the play button sprite
        gameScene.registerTouchArea(playButtonSprite);

        gameHud.attachChild(playButtonSprite);
    }

    // adds score to the game
    private void addScore(int pScore)
    {
        score += pScore;
        scoreText.setText(String.valueOf(score));
    }

    // adds a flappy bird that moves from right to left
    private void addFlappyBird()
    {
        // setting the x and y of each spawned flappy bird with random y
        flappyX = (int) (camera.getWidth() + redFlappyBirdRegion.getWidth());
        int minY = (int) (groundSprite.getHeight() + mayaBirdSprite.getHeight());
        int maxY = (int) (camera.getHeight() - (scoreText.getX() + (scoreText.getWidth() / 2)));
        int rangeY = maxY - minY;
        flappyY = (int) (Math.random() * rangeY) + minY;

        // creating and attaching flappy bird sprite
        int flappyBirdType = (int) (Math.round(Math.random() * 2));

        switch (flappyBirdType)
        {
            case 0:
                flappyBirdSprite = new AnimatedSprite(flappyX, flappyY,
                        camera.getWidth() * 0.03958f, camera.getHeight() * 0.0175f,
                        redFlappyBirdRegion.deepCopy(), getVertexBufferObjectManager());
                flappyBirdSprite.setUserData("red");
                break;
            default:
                flappyBirdSprite = new AnimatedSprite(flappyX, flappyY,
                        camera.getWidth() * 0.03958f, camera.getHeight() * 0.0175f,
                        yellowFlappyBirdRegion.deepCopy(), getVertexBufferObjectManager());
                flappyBirdSprite.setUserData("yellow");
                break;
        }

        flappyBirdSprite.setScale(2f);
        flappyBirdSprite.setFlippedHorizontal(true);
        flappyBirdSprite.animate(new long[]{100, 100, 100});

        gameScene.attachChild(flappyBirdSprite);

        int actualDuration = 5;
        MoveXModifier mod = new MoveXModifier(actualDuration, flappyBirdSprite.getX(), -flappyBirdSprite.getWidth());
        flappyBirdSprite.registerEntityModifier(mod.deepCopy());

        flappyBirdSpawner.add(flappyBirdSprite);
    }

    // creates the time handler that spawns flappy birds and the update handler that detects its collision and events
    private void createFlappyBirdSpawnTimeHandler()
    {
        flappyBirdSpawnTimerHandler = new TimerHandler(flappyBirdSpawnDelay, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                addFlappyBird();
            }
        });

        flappyBirdDetectUpdateHandler = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                Iterator<Sprite> flappyBirdList = flappyBirdDetector.iterator();
                Sprite flappyBird;

                while (flappyBirdList.hasNext())
                {
                    flappyBird = flappyBirdList.next();

                    if (!isGameOver && mayaBirdSprite.collidesWith(flappyBird))
                    {
                        // adds 1 score for every flappy bird
                        badingSound.play();

                        String flappyBirdType = flappyBird.getUserData().toString();

                        if (flappyBirdType.equals("red"))
                        {
                            addScore(RED_BIRD_SCORE);
                        }
                        else if (flappyBirdType.equals("yellow"))
                        {
                            addScore(YELLOW_BIRD_SCORE);
                        }

                        // stops the animation at the frame showing maya bird eating
                        mayaBirdSprite.stopAnimation(8);

                        // timer to start again animation of maya bird
                        gameScene.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
                        {
                            @Override
                            public void onTimePassed(TimerHandler pTimerHandler)
                            {
                                // animating maya bird sprite
                                mayaBirdSprite.animate(new long[]{100, 100, 100, 100, 100, 100, 100, 100});
                            }
                        }));

                        // destroys flappy bird and breaks process
                        destroyEntity(flappyBird);
                        flappyBirdList.remove();

                        break;
                    }
                    else if (!isGameOver && flappyBird.getX() <= -flappyBird.getWidth()
                            && flappyBird.getUserData().equals("yellow"))
                    {
                        isGameOver = true;

                        if (isGameOver)
                        {
                            gameOver();
                        }

                        break;
                    }
                }

                flappyBirdDetector.addAll(flappyBirdSpawner);
                flappyBirdSpawner.clear();
            }

            @Override
            public void reset()
            {
            }
        };
    }

    //-----------------------------------------
    // GAME MODIFIER METHODS
    //-----------------------------------------

    // fades in an entity with the specified duration
    private void fadeInEntity(Entity pEntity, float pDuration)
    {
        FadeInModifier fadeInMod = new FadeInModifier(pDuration);
        pEntity.registerEntityModifier(fadeInMod);
    }

    // fades out an entity with the specified duration
    private void fadeOutEntity(Entity pEntity, float pDuration)
    {
        FadeOutModifier fadeOutMod = new FadeOutModifier(pDuration);
        pEntity.registerEntityModifier(fadeOutMod);
    }

    // destroys an entity
    private void destroyEntity(Entity pEntity)
    {
        pEntity.setVisible(false);
        pEntity.detachSelf();
        pEntity.clearEntityModifiers();
        pEntity.clearUpdateHandlers();
        pEntity.dispose();
        pEntity = null;
    }

    //-----------------------------------------
    // GETTER AND SETTER METHODS
    //-----------------------------------------

    // gets the max score of the game
    private int getBestScore()
    {
        return this.getPreferences(Context.MODE_PRIVATE).getInt("bestScore", 0);
    }

    // sets the max score of the game
    private void setBestScore(int bestScore)
    {
        this.getPreferences(Context.MODE_PRIVATE).edit().putInt("bestScore", bestScore).commit();
    }
}
