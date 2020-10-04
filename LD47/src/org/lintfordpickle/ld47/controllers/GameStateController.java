package org.lintfordpickle.ld47.controllers;

import org.lintfordpickle.ld47.data.GameState;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class GameStateController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Game State Controller";

	public static boolean PREVIEW_MODE = true; // always spawn

	public final static String HEALTH_5 = "TOP HEALTH";
	public final static String HEALTH_4 = "DOING WELL";
	public final static String HEALTH_3 = "BIT SHAKEY";
	public final static String HEALTH_2 = "WINDED";
	public final static String HEALTH_1 = "LAST LEGS";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameState mGameState;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean getHasWon() {
		if (mGameState == null || PREVIEW_MODE)
			return false;
		return mGameState.timeRemaining <= 0.f;
	}

	public boolean getHasLost() {
		if (mGameState == null || PREVIEW_MODE)
			return false;
		return mGameState.playerHealth <= 0.f;
	}

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public GameState gameState() {
		return mGameState;
	}

	@Override
	public boolean isinitialized() {
		return mGameState != null;
	}

	// ---------------------------------------------
	// Construcotr
	// ---------------------------------------------

	public GameStateController(ControllerManager pControllerManager, GameState pGameState, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mGameState = pGameState;

	}

	// ---------------------------------------------
	// Core-Methods
	// --------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (!isinitialized())
			return;

		mGameState.timeAlive += pCore.gameTime().elapsedTimeMilli();
		mGameState.timeRemaining -= pCore.gameTime().elapsedTimeMilli();

		if (mGameState.timeRemaining < 0.f)
			mGameState.timeRemaining = 0.f;

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	public String getHealthStatus() {
		switch (mGameState.playerHealth) {
		case 5:
			return HEALTH_5;
		case 4:
			return HEALTH_4;
		case 3:
			return HEALTH_3;
		case 2:
			return HEALTH_2;
		default:
			return HEALTH_1;
		}
	}

}
