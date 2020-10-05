package org.lintfordpickle.ld47.controllers;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.lintfordpickle.ld47.data.physicsdata.NodePhysicsData;
import org.lintfordpickle.ld47.data.physicsdata.TrainPhysicsData;

import net.lintford.library.controllers.box2d.Box2dContactController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class GameCollisionController extends Box2dContactController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Game Collision Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameCollisionController(ControllerManager pControllerManager, World pWorld, int pEntityID) {
		super(pControllerManager, CONTROLLER_NAME, pWorld, pEntityID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		final var lControllerManager = pCore.controllerManager();

		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());

	}

	// ---------------------------------------------
	// Collision Methods
	// ---------------------------------------------

	@Override
	public void beginContact(Contact pContact) {
		if (pContact.getFixtureA().getBody().getUserData() instanceof TrainPhysicsData) {
			trainCollision(pContact, pContact.getFixtureA(), pContact.getFixtureB(), true);

		} else if (pContact.getFixtureB().getBody().getUserData() instanceof TrainPhysicsData) {
			trainCollision(pContact, pContact.getFixtureB(), pContact.getFixtureA(), true);

		}

	}

	@Override
	public void endContact(Contact arg0) {
		// TODO Auto-generated method stub

	}

	// ---------------------------------------------

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact pContact, Manifold arg1) {

		if (pContact.getFixtureA().getBody().getUserData() instanceof TrainPhysicsData) {
			if (pContact.getFixtureB().getBody().getUserData() instanceof NodePhysicsData) {
				pContact.setEnabled(false);

			} else if (pContact.getFixtureB().getBody().getUserData() instanceof TrainPhysicsData) {
				trainCollisionTrain(pContact, pContact.getFixtureA(), pContact.getFixtureB(), false);

			}
		}

		else if (pContact.getFixtureB().getBody().getUserData() instanceof TrainPhysicsData) {
			if (pContact.getFixtureA().getBody().getUserData() instanceof NodePhysicsData) {
				pContact.setEnabled(false);

			} else if (pContact.getFixtureA().getBody().getUserData() instanceof TrainPhysicsData) {
				trainCollisionTrain(pContact, pContact.getFixtureA(), pContact.getFixtureA(), false);

			}

		}

	}

	// ---------------------------------------------

	private void trainCollision(Contact pContact, Fixture pTrainFixture, Fixture pOtherFixture, boolean pCollides) {
		pContact.setEnabled(false);
//		if (pOtherFixture.getBody().getUserData() instanceof TrainPhysicsData) {
//			trainCollisionTrain(pContact, pTrainFixture, pOtherFixture, pCollides);
//
//		} else 
		if (pOtherFixture.getBody().getUserData() instanceof NodePhysicsData) {
			trainCollisionNode(pContact, pTrainFixture, pOtherFixture, pCollides);

		}

	}

	private void trainCollisionTrain(Contact pContact, Fixture pTrainFixture, Fixture pOtherTrainFixture, boolean pCollides) {
		final var lTrainAData = (TrainPhysicsData) pContact.getFixtureA().getBody().getUserData();
		final var lTrainBData = (TrainPhysicsData) pContact.getFixtureB().getBody().getUserData();

//		if (lTrainAData.train().trainNumber() == lTrainBData.train().trainNumber()) {
//			pContact.setEnabled(false);
//			return;
//		}

		boolean collisionWithPLayer = false;

		if (!lTrainAData.train().isPlayerControlled()) {
			lTrainAData.train().setHasHadCollision();

			final var lBodyA = pContact.getFixtureA().getBody();
			var lJointList = lBodyA.getJointList();

			while (lJointList != null) {
				mWorld.destroyJoint(lJointList.joint);
				lJointList = lJointList.next;
			}

			pContact.getFixtureA().getBody().setLinearDamping(.96f);

		} else {
			if (!lTrainBData.train().hasHadCollision()) {
				mGameStateController.gameState().playerHealth--;

			}
		}

		if (!lTrainBData.train().isPlayerControlled()) {
			lTrainBData.train().setHasHadCollision();

			final var lBodyB = pContact.getFixtureB().getBody();
			var lJointList = lBodyB.getJointList();
			while (lJointList != null) {
				mWorld.destroyJoint(lJointList.joint);
				lJointList = lJointList.next;

			}

			pContact.getFixtureB().getBody().setLinearDamping(.96f);

		} else {
			if (!lTrainAData.train().hasHadCollision()) {
				mGameStateController.gameState().playerHealth--;

			}
		}

		if (!collisionWithPLayer) {
			pContact.setEnabled(true);
		}

	}

	private void trainCollisionNode(Contact pContact, Fixture pTrainFixture, Fixture pNodeFixture, boolean pCollides) {
		final var lTrainAData = (TrainPhysicsData) pTrainFixture.getBody().getUserData();
		final var lNodeData = (NodePhysicsData) pNodeFixture.getBody().getUserData();

		if (pTrainFixture.getUserData() instanceof String) {
			if (pTrainFixture.getUserData().equals("SensorFixture")) {
				return;

			}
		}

		if (lTrainAData.train() != null) {
			lTrainAData.train().setHasArrived(lNodeData.poolUid);

		}

	}

}
