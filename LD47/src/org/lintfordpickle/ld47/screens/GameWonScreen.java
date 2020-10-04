package org.lintfordpickle.ld47.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class GameWonScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String WINDOW_TITLE = "Game Won";

	private static final int BUTTON_CONTINUE = 0;
	private static final int BUTTON_REPLAY = 1;
	private static final int BUTTON_END = 2;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWonScreen(ScreenManager pScreenManager) {
		super(pScreenManager, WINDOW_TITLE);
		ListLayout lButtonsLayout = new ListLayout(this);

		MenuEntry lButtonResume = new MenuEntry(mScreenManager, lButtonsLayout, "Resume");
		MenuEntry lButtonReplay = new MenuEntry(mScreenManager, lButtonsLayout, "Replay");
		MenuEntry lButtonEnd = new MenuEntry(mScreenManager, lButtonsLayout, "Main Menu");

		// register clicks
		lButtonResume.registerClickListener(this, BUTTON_CONTINUE);
		lButtonReplay.registerClickListener(this, BUTTON_REPLAY);
		lButtonEnd.registerClickListener(this, BUTTON_END);

		mFooterLayout.menuEntries().add(lButtonResume);
		mFooterLayout.menuEntries().add(lButtonReplay);
		mFooterLayout.menuEntries().add(lButtonEnd);

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

		case BUTTON_REPLAY:
			LoadingScreen.load(mScreenManager, true, new GameScreen(mScreenManager));
			break;

		case BUTTON_END:
			LoadingScreen.load(mScreenManager, false, new BackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));
			break;

		}

	}
}
