package org.lintfordpickle.ld47.data.train;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.instances.PooledInstanceManager;

public class TrainManager extends PooledInstanceManager<Train> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 125458359688874600L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mTrainUidCounter = 0;
	private List<Train> mActiveTrains;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getNewTrainUid() {
		return mTrainUidCounter++;
	}

	public List<Train> activeTrains() {
		return mActiveTrains;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrainManager() {
		mActiveTrains = new ArrayList<>();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected Train createPoolObjectInstance() {
		return new Train(getNewTrainUid());

	}

}
