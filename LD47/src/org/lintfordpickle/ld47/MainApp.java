package org.lintfordpickle.ld47;

import org.lintfordpickle.ld47.screens.BackgroundScreen;
import org.lintfordpickle.ld47.screens.GameScreen;
import org.lintfordpickle.ld47.screens.MainMenuScreen;
import org.lintfordpickle.ld47.screens.TrackEditorScreen;

import net.lintford.library.GameInfo;
import net.lintford.library.controllers.core.MouseCursorController;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.controllers.music.MusicController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.screenmanager.IMenuAction;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.TimedIntroScreen;

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

		// Quick launch
		if (GameConstants.QUICK_LAUNCH_GAME) {
			mScreenManager.addScreen(new GameScreen(mScreenManager));

		} else if (GameConstants.QUICK_LAUNCH_EDITOR) {
			mScreenManager.addScreen(new TrackEditorScreen(mScreenManager));

		} else {
			if (!GameConstants.QUICK_LAUNCH_SKIP_INTRO) {
				final var lSplashScreen = new TimedIntroScreen(mScreenManager, "res/textures/screens/textureScreenSplash.png", 4f);
				lSplashScreen.stretchBackgroundToFit(true);

				lSplashScreen.setTimerFinishedCallback(new IMenuAction() {

					@Override
					public void TimerFinished(Screen pScreen) {
						mScreenManager.addScreen(new BackgroundScreen(mScreenManager));
						mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

					}

				});

				mScreenManager.addScreen(lSplashScreen);

			} else {
				// Normal start
				mScreenManager.addScreen(new BackgroundScreen(mScreenManager));
				mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

			}

		}

		mScreenManager.initialize();

	}

	@Override
	protected void onLoadGLContent() {
		super.onLoadGLContent();

		mResourceManager.pobjectManager().definitionRepository().loadDefinitionFromFile("Train", "res/pobjects/pobjectTrain.json", false);

		mScreenManager.loadGLContent(mResourceManager);

		mResourceManager.musicManager().loadMusicFromMetaFile("res/music/meta.json");

		final var lControlerManager = mScreenManager.core().controllerManager();
		new MusicController(lControlerManager, mResourceManager.musicManager(), LintfordCore.CORE_ENTITY_GROUP_ID);

		// lMusic.nextSong();

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
