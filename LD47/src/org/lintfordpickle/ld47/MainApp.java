package org.lintfordpickle.ld47;

import org.lintfordpickle.ld47.screens.BackgroundScreen;
import org.lintfordpickle.ld47.screens.MainMenuScreen;

import net.lintford.library.GameInfo;
import net.lintford.library.controllers.core.MouseCursorController;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.screenmanager.ScreenManager;

public class MainApp extends LintfordCore {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String APPLICAITON_NAME = "LD47";
	private static final String WINDOW_TITLE = "LD47 - Stuck in a Loop";

	// ---------------------------------------------
	// Entry
	// ---------------------------------------------

	public static void main(String[] pArgs) {

		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return APPLICAITON_NAME;
			}

			@Override
			public String windowTitle() {
				return WINDOW_TITLE;
			}

			@Override
			public boolean windowResizeable() {
				return GameConstants.WINDOW_RESIZABLE;
			}
		};

		final var lMainApp = new MainApp(lGameInfo, pArgs);
		lMainApp.createWindow();

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ScreenManager mScreenManager;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MainApp(GameInfo pGameInfo, String[] pArgs) {
		super(pGameInfo, pArgs, false);

		mScreenManager = new ScreenManager(this);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void onInitializeApp() {
		super.onInitializeApp();

		// Custom mouse
		var lMouseController = new MouseCursorController(mControllerManager, CORE_ENTITY_GROUP_ID);
		lMouseController.initialize(this);

		var lResourceController = new ResourceController(mControllerManager, mResourceManager, CORE_ENTITY_GROUP_ID);
		lResourceController.initialize(this);

		// FIXME: Before release, re-enable menu
		mScreenManager.addScreen(new BackgroundScreen(mScreenManager));
		mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

		// mScreenManager.addScreen(new GameScreen(mScreenManager));
		// mScreenManager.addScreen(new TrackEditorScreen(mScreenManager));

		mScreenManager.initialize();

	}

	@Override
	protected void onLoadGLContent() {
		super.onLoadGLContent();

		mResourceManager.pobjectManager().definitionRepository().loadDefinitionFromFile("Train", "res/pobjects/pobjectTrain.json", false);

		mScreenManager.loadGLContent(mResourceManager);

	}

	@Override
	protected void onUnloadGLContent() {
		super.onUnloadGLContent();

		mScreenManager.unloadGLContent();

	}

	@Override
	protected void onHandleInput() {
		super.onHandleInput();

		mScreenManager.handleInput(this);

	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		mScreenManager.update(this);

	}

	@Override
	protected void onDraw() {
		super.onDraw();

		mScreenManager.draw(this);

	}
}
