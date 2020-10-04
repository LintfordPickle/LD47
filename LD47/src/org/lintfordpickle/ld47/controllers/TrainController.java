package org.lintfordpickle.ld47.controllers;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lintfordpickle.ld47.data.GameWorld;
import org.lintfordpickle.ld47.data.physicsdata.TrainPhysicsData;
import org.lintfordpickle.ld47.data.track.Edge;
import org.lintfordpickle.ld47.data.train.Train;
import org.lintfordpickle.ld47.data.train.TrainManager;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;

public class TrainController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Train Controller";

	private static final float TRAIN_LENGTH_IN_PIXELS = 16.0f;
	private static final float TRAIN_WIDTH_IN_PIXELS = 8.0f;

	private static final boolean FORCE_NO_CARRIAGES_BECAUSE_BROKEN = true;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Box2dGameController mBox2dGameController;
	private GameStateController mGameStateController;

	private TrackController mTrackController;
	private TrainManager mTrainManager;
	private Train mMainTrain;

	private List<Train> mUpdateTrainList = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isinitialized() {
		return mTrainManager != null;
	}

	public TrainManager trainManager() {
		return mTrainManager;
	}

	public Train mainTrain() {
		return mMainTrain;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrainController(ControllerManager pControllerManager, GameWorld pGameWorld, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mTrainManager = pGameWorld.trainManager();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

		mTrackController = (TrackController) lControllerManager.getControllerByNameRequired(TrackController.CONTROLLER_NAME, entityGroupID());
		mBox2dGameController = (Box2dGameController) pCore.controllerManager().getControllerByNameRequired(Box2dGameController.CONTROLLER_NAME, entityGroupID());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		final var lActiveTrains = mTrainManager.activeTrains();
		final var lActiveTrainCount = lActiveTrains.size();

		mUpdateTrainList.clear();
		for (int i = 0; i < lActiveTrainCount; i++) {
			final var lTrain = lActiveTrains.get(i);

			mUpdateTrainList.add(lTrain);

		}

		for (int i = 0; i < lActiveTrainCount; i++) {
			final var lTrain = mUpdateTrainList.get(i);
			if (lTrain.isCarriage)
				continue;

			// FIXME:
			if (lTrain.hasHadCollision()) {
				if (!lTrain.wasPrisJointDestroyed() && lTrain.pullJoint() != null) {
					final var lBox2dWorld = mBox2dGameController.world();
					lBox2dWorld.destroyJoint(lTrain.pullJoint());
					lTrain.wasPrisJointDestroyed(true);

				}

				// Check the time for cleanup
				final float TIME_TILL_REMOVE = 2500.f;
				if (lTrain.timeSinceCollision() > TIME_TILL_REMOVE) {
					mGameStateController.gameState().numberCollisions++;
					removeTrain(lTrain);
					continue;

				}

			} else {

				if (lTrain.hasArrived()) {
					sendTrainToNextDestination(lTrain);

				} else {
					// Manually check the distance
					final var lDestinationNode = lTrain.destinationNode();
					final var lDistanceToDestination = Vector2f.distance(lDestinationNode.worldPositionX, lDestinationNode.worldPositionY, lTrain.worldPositionX(), lTrain.worldPositionY());
					if (lDistanceToDestination < 4.0f) {
						sendTrainToNextDestination(lTrain);
					}
				}

			}

			lTrain.update(pCore);
		}

	}

	public Train addNewMainTrain(int pSpawnNodeUid) {
		mMainTrain = addNewTrain(pSpawnNodeUid, 1);
		return mMainTrain;
	}

	public Train addNewTrain(int pSpawnNodeUid) {
		return addNewTrain(pSpawnNodeUid, 1);

	}

	public Train addNewTrain(int pSpawnNodeUid, int pNumCarriages) {
		if (FORCE_NO_CARRIAGES_BECAUSE_BROKEN)
			pNumCarriages = 0;

		final var lTrack = mTrackController.track();
		final Train lNewTrain = mTrainManager.getFreePooledItem();
		lNewTrain.isCarriage = false;

		final var lTrainObject = mBox2dGameController.getTrainPhysicsObject(0, 0, TRAIN_LENGTH_IN_PIXELS, TRAIN_WIDTH_IN_PIXELS);
		lTrainObject.userDataObject(new TrainPhysicsData(lNewTrain));

		lNewTrain.setPhysicsObject(lTrainObject);

		final var lSpawnNode = lTrack.getNodeByUid(pSpawnNodeUid);
		lNewTrain.setLocation(lSpawnNode, true);

		JBox2dEntityInstance lLeadingTrain = lNewTrain.physicsObject(); // Main engine
		for (int i = 0; i < pNumCarriages; i++) {
			final Train lNewTrainCarriage = mTrainManager.getFreePooledItem();
			lNewTrainCarriage.isCarriage = false;

			final var lTrainCarriageObject = mBox2dGameController.getTrainCarriagePhysicsObject(lLeadingTrain, TRAIN_LENGTH_IN_PIXELS, TRAIN_WIDTH_IN_PIXELS);

			lNewTrainCarriage.setPhysicsObject(lTrainCarriageObject);

			mBox2dGameController.connectTrainCarriages(lNewTrain, lNewTrainCarriage);

			lLeadingTrain = lTrainCarriageObject;

		}

		mTrainManager.activeTrains().add(lNewTrain);

		sendTrainToNextDestination(lNewTrain);

		return lNewTrain;
	}

	public void removeTrain(Train pTrain) {
		if (pTrain == null)
			return;

		if (pTrain.hasPhysicsObject()) {
			if (pTrain.pullJoint() != null) {
				final var lBox2dWorld = mBox2dGameController.world();
				lBox2dWorld.destroyJoint(pTrain.pullJoint());
			}

			pTrain.physicsObject().unloadPhysics();

		}

		final var lTrainsList = mTrainManager.activeTrains();
		if (lTrainsList.contains(pTrain)) {
			lTrainsList.remove(pTrain);

		}

		pTrain.cleanUp();

		mTrainManager.returnPooledItem(pTrain);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void sendTrainToNextDestination(Train pTrain) {
		final var lTrack = mTrackController.track();

		final var lCurrentNode = pTrain.destinationNode();

		final var lNextEdge = getNextEdge(pTrain);
		if (lNextEdge == null) {
			pTrain.setHasHadCollision();
			return;
		}

		final var lOtherNodeUidOnEdge = lNextEdge.getOtherNodeUid(lCurrentNode.poolUid);
		final var lDestinationNode = lTrack.getNodeByUid(lOtherNodeUidOnEdge);

		float lVectorX = lDestinationNode.worldPositionX - pTrain.worldPositionX();
		float lVectorY = lDestinationNode.worldPositionY - pTrain.worldPositionY();

		final var lTrainAngle = (float) Math.atan2(lVectorY, lVectorX);

		// Just update the rotation
		// FIXME: not simulating physics properly = breaks carriages
		pTrain.physicsObject().transformEntityInstance(pTrain.worldPositionX(), pTrain.worldPositionY(), lTrainAngle);

		if (pTrain.pullJoint() != null) {
			mBox2dGameController.world().destroyJoint(pTrain.pullJoint());

		}

		// update revolute join angles for carriages
		if (pTrain.mRevJointPulling != null) {

		}

		// pTrain.setLocation(lCurrentNode);
		final var lForce = pTrain.getForce();
		final var lSpeed = pTrain.getSpeed();

		final var lPullJoint = mBox2dGameController.getPrismaticJointToNode(pTrain, lDestinationNode, lForce, lSpeed);

		pTrain.setDestination(lPullJoint, lNextEdge, lDestinationNode, false);

	}

	private Edge getNextEdge(Train pTrain) {
		Edge lReturnEdge = null;
		final var lCurrentNode = pTrain.destinationNode();
		if (lCurrentNode == null)
			return null;
		if (pTrain.currentEdge() == null) {
			lReturnEdge = lCurrentNode.getRandomEdge();

		} else {
			final var lCurrentEdge = pTrain.currentEdge();

			if (lCurrentEdge.signalNode != null && lCurrentEdge.signalNode.isSignalActive && lCurrentNode.poolUid == lCurrentEdge.signalNode.signalNodeUid) {
				final var lActiveEdgeUid = lCurrentEdge.signalNode.leftEnabled ? lCurrentEdge.signalNode.leftEdgeUid : lCurrentEdge.signalNode.rightEdgeUid;
				return mTrackController.track().getEdgeByUid(lActiveEdgeUid);

			}

			lReturnEdge = lCurrentNode.getRandomEdgeApartFrom(lCurrentEdge.allowedEdgeConections, lCurrentEdge.uid);
			if (lReturnEdge == null)
				lReturnEdge = lCurrentEdge;

		}

		return lReturnEdge;
	}

}
