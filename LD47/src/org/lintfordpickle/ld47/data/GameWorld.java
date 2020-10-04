package org.lintfordpickle.ld47.data;

import org.lintfordpickle.ld47.data.track.Track;
import org.lintfordpickle.ld47.data.train.TrainManager;

public class GameWorld {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TrainManager mTrainManager;
	private Track mTrack;
	private WorldScenery mWorldScenery;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public Track track() {
		return mTrack;
	};

	public void track(Track pTrack) {
		mTrack = pTrack;
	};

	public WorldScenery worldScenery() {
		return mWorldScenery;
	};

	public void worldScenery(WorldScenery pWorldScenery) {
		mWorldScenery = pWorldScenery;
	};

	public TrainManager trainManager() {
		return mTrainManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWorld() {
		mTrainManager = new TrainManager();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
