package org.lintfordpickle.ld47.screens;

import org.lintfordpickle.ld47.controllers.CameraMovementController;
import org.lintfordpickle.ld47.controllers.CameraZoomController;
import org.lintfordpickle.ld47.controllers.SceneryController;
import org.lintfordpickle.ld47.controllers.TrackEditorController;
import org.lintfordpickle.ld47.renderers.SceneryRenderer;
import org.lintfordpickle.ld47.renderers.TrackEditorRenderer;
import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.BaseGameScreen;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class TrackEditorScreen extends BaseGameScreen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// Data

	// Controllers
	private CameraMovementController mCameraMovementController;
	private CameraZoomController mCameraZoomController;
	private TrackEditorController mTrackEditorController;
	private SceneryController mSceneryController;

	// Renderers
	private TrackEditorRenderer mTrackEditorRenderer;
	private SceneryRenderer mSceneryRenderer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrackEditorScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mShowInBackground = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final var lCore = mScreenManager.core();
		final var lControllerManager = lCore.controllerManager();

		createControllers(lControllerManager);
		initializeControllers(lCore);

		createRenderers(lCore);

		mSceneryController.loadDefaultScene("res/scenery/sceneryTest.json");
		mTrackEditorController.loadDefaultScene("res/tracks/trackTest.json");

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		pResourceManager.spriteSheetManager().loadSpriteSheet("res/spritesheets/spritesheetWorld.json", entityGroupID());

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (!pAcceptKeyboard)
			return;

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F5)) {

			final String lTrackFilename = "res/tracks/trackTest.json";
			final String lSceneryFilename = "res/scenery/sceneryTest.json";

			System.out.println("Saving track to " + lTrackFilename);
			System.out.println("Saving scenery to " + lSceneryFilename);

			mTrackEditorController.saveTrack(lTrackFilename);
			mSceneryController.saveSceneryScene(lSceneryFilename);

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			LoadingScreen.load(mScreenManager, false, new BackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));
			return;

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void createControllers(ControllerManager pControllerManager) {
		mCameraMovementController = new CameraMovementController(pControllerManager, mGameCamera, entityGroupID());
		mCameraMovementController.setPlayArea(-1200, -800, 2400, 1600);
		mCameraZoomController = new CameraZoomController(pControllerManager, mGameCamera, entityGroupID());
		mCameraZoomController.setZoomConstraints(200, 900);

		mTrackEditorController = new TrackEditorController(pControllerManager, entityGroupID());
		mSceneryController = new SceneryController(pControllerManager, null, entityGroupID());

	}

	public void initializeControllers(LintfordCore pCore) {
		mTrackEditorController.initialize(pCore);
		mSceneryController.initialize(pCore);

		mCameraMovementController.initialize(pCore);
		mCameraZoomController.initialize(pCore);

	}

	public void createRenderers(LintfordCore pCore) {
		final var lRendererManager = mRendererManager;

		mTrackEditorRenderer = new TrackEditorRenderer(lRendererManager, entityGroupID());
		mTrackEditorRenderer.initialize(pCore);

		mSceneryRenderer = new SceneryRenderer(lRendererManager, entityGroupID());
		mSceneryRenderer.initialize(pCore);

	}

}
