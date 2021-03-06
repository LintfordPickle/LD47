package org.lintfordpickle.ld47.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class GameLostScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String WINDOW_TITLE = "Game Lost";

	private static final int BUTTON_RETRY = 0;
	private static final int BUTTON_END = 1;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameLostScreen(ScreenManager pScreenManager) {
		super(pScreenManager, WINDOW_TITLE);

		ListLayout lButtonsLayout = new ListLayout(this);

		MenuEntry lButtonRetry = new MenuEntry(mScreenManager, lButtonsLayout, "Retry");
		MenuEntry lButtonEnd = new MenuEntry(mScreenManager, lButtonsLayout, "Exit to Menu");

		// register clicks
		lButtonRetry.registerClickListener(this, BUTTON_RETRY);
		lButtonEnd.registerClickListener(this, BUTTON_END);

		lButtonsLayout.menuEntries().add(lButtonRetry);
		lButtonsLayout.menuEntries().add(lButtonEnd);

		layouts().add(lButtonsLayout);

		mIsPopup = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_RETRY:
			LoadingScreen.load(mScreenManager, false, new GameScreen(mScreenManager));
			break;

		case BUTTON_END:
			LoadingScreen.load(mScreenManager, false, new BackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));
			break;

		}

	}
}
