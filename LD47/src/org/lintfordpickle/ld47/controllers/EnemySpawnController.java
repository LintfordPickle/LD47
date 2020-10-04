package org.lintfordpickle.ld47.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;

public class EnemySpawnController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String CONTROLLER_NAME = "Enemy Spawn Controller";

	private static boolean ENABLE_ENEMY_SPAWNING = false; // TODO: enemy spawn on

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TrackController mTrackController;
	private TrainController mTrainController;
	private GameStateController mGameStateController;

	private float mEnemySpawnTimer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isinitialized() {
		return mTrainController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EnemySpawnController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mTrainController = (TrainController) pCore.controllerManager().getControllerByNameRequired(TrainController.CONTROLLER_NAME, entityGroupID());
		mGameStateController = (GameStateController) pCore.controllerManager().getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
		mTrackController = (TrackController) pCore.controllerManager().getControllerByNameRequired(TrackController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (!isinitialized())
			return;

		if (!ENABLE_ENEMY_SPAWNING)
			return;

		if (!GameStateController.PREVIEW_MODE && (mGameStateController.getHasWon() || mGameStateController.getHasLost()))
			return;

		// TODO: do correct timing for spawn (node based timing)

		mEnemySpawnTimer -= pCore.gameTime().elapsedTimeMilli();
		if (mEnemySpawnTimer < 0.0f) {
			final var lTrack = mTrackController.track();
			if (lTrack.enemySpawnNodes.size() > 0) {
				System.out.println("SPAWNING NEW ENEMY");
				final var lSpawnNodeUid = lTrack.enemySpawnNodes.get(RandomNumbers.random(0, lTrack.enemySpawnNodes.size()));
				mTrainController.addNewTrain(lSpawnNodeUid, 0);

			} else {
				mEnemySpawnTimer = 2000000.f;

			}

			mEnemySpawnTimer = 10000.f;

		}

	}

}
