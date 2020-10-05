package org.lintfordpickle.ld47.screens;

import org.lintfordpickle.ld47.GameConstants;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class MainMenuScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String WINDOW_TITLE = "";

	private static final int BUTTON_START = 0;
	private static final int BUTTON_TUTORIAL = 1;
	private static final int BUTTON_EDITOR = 2;
	private static final int BUTTON_END = 3;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MainMenuScreen(ScreenManager pScreenManager) {
		super(pScreenManager, WINDOW_TITLE);

		ListLayout lButtonsLayout = new ListLayout(this);

		MenuEntry lButtonStart = new MenuEntry(mScreenManager, lButtonsLayout, "Start Game");
		MenuEntry lButtonTutorial = new MenuEntry(mScreenManager, lButtonsLayout, "How to Play");
		MenuEntry lButtonStartEditor = new MenuEntry(mScreenManager, lButtonsLayout, "Track Editor");
		MenuEntry lButtonEnd = new MenuEntry(mScreenManager, lButtonsLayout, "Exit");

		// register clicks
		lButtonStart.registerClickListener(this, BUTTON_START);
		lButtonTutorial.registerClickListener(this, BUTTON_TUTORIAL);
		lButtonStartEditor.registerClickListener(this, BUTTON_EDITOR);
		lButtonEnd.registerClickListener(this, BUTTON_END);

		lButtonsLayout.menuEntries().add(lButtonStart);
		lButtonsLayout.menuEntries().add(lButtonTutorial);
		if (GameConstants.ENABLE_TRACK_EDITOR_IN_MENU)
			lButtonsLayout.menuEntries().add(lButtonStartEditor);
		lButtonsLayout.menuEntries().add(lButtonEnd);

		layouts().add(lButtonsLayout);

		mESCBackEnabled = false;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_START:
			LoadingScreen.load(mScreenManager, true, new GameScreen(mScreenManager));
			break;

		case BUTTON_TUTORIAL:
			mScreenManager.addScreen(new TutorialMenuScreen(mScreenManager));
			break;

		case BUTTON_EDITOR:
			LoadingScreen.load(mScreenManager, true, new TrackEditorScreen(mScreenManager));
			break;

		case BUTTON_END:
			mScreenManager.exitGame();
			break;
		}
	}

}
