package org.lintfordpickle.ld47.data.physicsdata;

import org.lintfordpickle.ld47.data.train.Train;

import net.lintford.library.core.box2d.BasePhysicsData;

public class TrainPhysicsData extends BasePhysicsData {

	private static final long serialVersionUID = -7851623555717451320L;

	private Train mTrain;

	public Train train() {
		return mTrain;
	}

	public TrainPhysicsData(Train pTrain) {
		super(pTrain.poolUid);

		mTrain = pTrain;

	}

	@Override
	public void reset() {
		mTrain = null;

	}

}
