package org.lintfordpickle.ld47.screens;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lintfordpickle.ld47.GameConstants;
import org.lintfordpickle.ld47.controllers.Box2dGameController;
import org.lintfordpickle.ld47.controllers.CameraMovementController;
import org.lintfordpickle.ld47.controllers.CameraZoomController;
import org.lintfordpickle.ld47.controllers.EnemySpawnController;
import org.lintfordpickle.ld47.controllers.GameCollisionController;
import org.lintfordpickle.ld47.controllers.GameStateController;
import org.lintfordpickle.ld47.controllers.GameWorldController;
import org.lintfordpickle.ld47.controllers.SceneryController;
import org.lintfordpickle.ld47.controllers.TrackController;
import org.lintfordpickle.ld47.controllers.TrainController;
import org.lintfordpickle.ld47.data.GameState;
import org.lintfordpickle.ld47.data.GameWorld;
import org.lintfordpickle.ld47.renderers.BackgroundRenderer;
import org.lintfordpickle.ld47.renderers.SceneryRenderer;
import org.lintfordpickle.ld47.renderers.TrackRenderer;
import org.lintfordpickle.ld47.renderers.TrainRenderer;
import org.lintfordpickle.ld47.renderers.ui.GameStateUIRenderer;
import org.lintfordpickle.ld47.renderers.ui.PlayerControlsRenderer;
import org.lintfordpickle.ld47.services.SceneryLoader;
import org.lintfordpickle.ld47.services.TrackLoader;
import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.renderers.debug.DebugBox2dDrawer;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.BaseGameScreen;

public class GameScreen extends BaseGameScreen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// Data
	private World mBox2dWorld;
	private GameState mGameState;
	private GameWorld mGameWorld;

	// Controllers
	private CameraMovementController mCameraMovementController;
	private CameraZoomController mCameraZooomController;

	private GameStateController mGameStateController;
	private GameWorldController mGameWorldController;
	private Box2dGameController mBox2dWorldController;
	private GameCollisionController mGameCollisionController;
	private TrackController mTrackController;
	private TrainController mTrainController;
	private EnemySpawnController mEnemySpawnController;
	private SceneryController mSceneryController;

	// Renderers
	private TrackRenderer mTrackRenderer;
	private TrainRenderer mTrainRenderer;
	private DebugBox2dDrawer mBox2dDebugDrawer;
	private PlayerControlsRenderer mPlayerControlsRenderer;
	private BackgroundRenderer mBackgroundRenderer;
	private GameStateUIRenderer mGameStateUIRenderer;
	private SceneryRenderer mSceneryRenderer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mShowInBackground = true;

		final var lGravity = new Vec2(0.f, 0.f);
		mBox2dWorld = new World(lGravity);

		mGameWorld = new GameWorld();
		mGameState = new GameState(0); // TODO: once per screen ?

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final var lCore = mScreenManager.core();
		final var lControllerManager = lCore.controllerManager();

		var lTestTrack = TrackLoader.loadTrackFromFile("res/tracks/trackTest.json");
		var lWorldScenery = SceneryLoader.loadSceneryFromFile("res/scenery/sceneryTest.json");

		mGameWorld.track(lTestTrack);
		mGameWorld.worldScenery(lWorldScenery);

		createControllers(lControllerManager);
		initializeControllers(lCore);

		lTestTrack = TrackLoader.createPhysicsObjects(lTestTrack, lControllerManager, entityGroupID());

		mGameWorldController.startNewGame();
		mGameState.startNewGame(300000); // 300000 ms = 5 mins

		createRenderers(lCore);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		pResourceManager.spriteSheetManager().loadSpriteSheet("res/spritesheets/spritesheetWorld.json", entityGroupID());

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (!pAcceptKeyboard)
			return;

		// TODO: some kind of editor check
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F8)) {
			mSceneryController.saveSceneryScene("res/scenery/sceneryTest.json");
			return;
		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_P) || pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			mScreenManager.addScreen(new PauseScreen(mScreenManager));
			return;
		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!pOtherScreenHasFocus && !pCoveredByOtherScreen) {
			if (mGameStateController.getHasWon()) {
				mScreenManager.addScreen(new GameWonScreen(mScreenManager));
				return;
			}

			if (mGameStateController.getHasLost()) {
				mScreenManager.addScreen(new GameLostScreen(mScreenManager));
				return;
			}

		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		if (mCameraMovementController != null)
			Debug.debugManager().drawers().drawRectImmediate(pCore.gameCamera(), mCameraMovementController.playArea());

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void createControllers(ControllerManager pControllerManager) {
		mGameStateController = new GameStateController(pControllerManager, mGameState, entityGroupID());
		mBox2dWorldController = new Box2dGameController(pControllerManager, mBox2dWorld, entityGroupID());
		mGameWorldController = new GameWorldController(pControllerManager, mGameWorld, entityGroupID());
		mGameCollisionController = new GameCollisionController(pControllerManager, mBox2dWorld, entityGroupID());
		mTrackController = new TrackController(pControllerManager, mGameWorld, entityGroupID());
		mTrainController = new TrainController(pControllerManager, mGameWorld, entityGroupID());
		mEnemySpawnController = new EnemySpawnController(pControllerManager, entityGroupID());
		mSceneryController = new SceneryController(pControllerManager, mGameWorld.worldScenery(), entityGroupID());

		mCameraMovementController = new CameraMovementController(pControllerManager, mGameCamera, entityGroupID());
		mCameraMovementController.setPlayArea(-1200, -800, 2400, 1600);
		mCameraZooomController = new CameraZoomController(pControllerManager, mGameCamera, entityGroupID());
		mCameraZooomController.setZoomConstraints(300, 900);

	}

	public void initializeControllers(LintfordCore pCore) {
		mBox2dWorldController.initialize(pCore);
		mGameStateController.initialize(pCore);
		mGameWorldController.initialize(pCore);
		mGameCollisionController.initialize(pCore);
		mTrackController.initialize(pCore);
		mTrainController.initialize(pCore);
		mEnemySpawnController.initialize(pCore);
		mSceneryController.initialize(pCore);

		mCameraMovementController.initialize(pCore);
		mCameraZooomController.initialize(pCore);

	}

	public void createRenderers(LintfordCore pCore) {
		final var lRendererManager = mRendererManager;

		mTrackRenderer = new TrackRenderer(lRendererManager, entityGroupID());
		mTrackRenderer.initialize(pCore);

		mTrainRenderer = new TrainRenderer(lRendererManager, entityGroupID());
		mTrainRenderer.initialize(pCore);

		if (GameConstants.ENABLE_BOX2D_DEBUG_DRAW) {
			mBox2dDebugDrawer = new DebugBox2dDrawer(lRendererManager, mBox2dWorld, entityGroupID());
			mBox2dDebugDrawer.isActive(true);

		}

		mSceneryRenderer = new SceneryRenderer(mRendererManager, entityGroupID());
		mSceneryRenderer.initialize(pCore);

		// UI -----

		mPlayerControlsRenderer = new PlayerControlsRenderer(mRendererManager, entityGroupID());
		mPlayerControlsRenderer.initialize(pCore);

		mBackgroundRenderer = new BackgroundRenderer(mRendererManager, entityGroupID());
		mBackgroundRenderer.initialize(pCore);

		mGameStateUIRenderer = new GameStateUIRenderer(mRendererManager, entityGroupID());
		mGameStateUIRenderer.initialize(pCore);

	}

}
