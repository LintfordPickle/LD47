package org.lintfordpickle.ld47.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class PauseScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String WINDOW_TITLE = "Paused";

	private static final int BUTTON_CONTINUE = 0;
	private static final int BUTTON_RESTART = 3;
	private static final int BUTTON_END = 1;
	private static final int BUTTON_EXIT = 2;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PauseScreen(ScreenManager pScreenManager) {
		super(pScreenManager, WINDOW_TITLE);

		ListLayout lButtonsLayout = new ListLayout(this);

		MenuEntry lButtonResume = new MenuEntry(mScreenManager, lButtonsLayout, "Resume");
		MenuEntry lButtonRestart = new MenuEntry(mScreenManager, lButtonsLayout, "Restart");
		MenuEntry lButtonEnd = new MenuEntry(mScreenManager, lButtonsLayout, "Exit to Menu");
		MenuEntry lButtonExit = new MenuEntry(mScreenManager, lButtonsLayout, "Exit Desktop");

		// register clicks
		lButtonResume.registerClickListener(this, BUTTON_CONTINUE);
		lButtonRestart.registerClickListener(this, BUTTON_RESTART);
		lButtonEnd.registerClickListener(this, BUTTON_END);
		lButtonExit.registerClickListener(this, BUTTON_EXIT);

		lButtonsLayout.menuEntries().add(lButtonResume);
		lButtonsLayout.menuEntries().add(lButtonRestart);
		lButtonsLayout.menuEntries().add(lButtonEnd);
		lButtonsLayout.menuEntries().add(lButtonExit);

		layouts().add(lButtonsLayout);

		mIsPopup = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CONTINUE:
			exitScreen();
			break;

		case BUTTON_RESTART:
			LoadingScreen.load(mScreenManager, true, new GameScreen(mScreenManager));
			break;

		case BUTTON_END:
			LoadingScreen.load(mScreenManager, false, new BackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));
			break;

		case BUTTON_EXIT:
			mScreenManager.exitGame();
			break;

		}

	}
}
