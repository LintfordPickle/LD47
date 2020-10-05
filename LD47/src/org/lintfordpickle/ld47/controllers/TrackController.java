package org.lintfordpickle.ld47.controllers;

import org.lintfordpickle.ld47.data.GameWorld;
import org.lintfordpickle.ld47.data.track.Track;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class TrackController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Track Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------
	
	private GameWorld mGameWorld;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isinitialized() {
		return mGameWorld != null;
	}

	public Track track() {
		return mGameWorld.track();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrackController(ControllerManager pControllerManager, GameWorld pGameWorld, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mGameWorld = pGameWorld;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

}
