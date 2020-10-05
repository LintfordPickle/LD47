package org.lintfordpickle.ld47.controllers;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.lintfordpickle.ld47.data.track.Node;
import org.lintfordpickle.ld47.data.train.Train;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.maths.Vector2f;

public class Box2dGameController extends Box2dWorldController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Box2d Game Controller";

	public static final int CATEGORY_TRAIN = 0b00000001;
	public static final int CATEGORY_NODE = 0b00000010;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Box2dGameController(ControllerManager pControllerManager, World pWorld, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pWorld, pEntityGroupID);
		// TODO Auto-generated constructor stub
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public JBox2dEntityInstance getNodePhysicsObject(float pWorldRadius) {
		if (!isinitialized()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot retreive a new ObjectBox2dEntityInstance - Box2dWorldController is not initialized!");
			return null;

		}

		final var lNodeJBox2dEntity = mResourceController.resourceManager().pobjectManager().getNewInstanceCircleInstance(mWorld, Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC, pWorldRadius);

		if (lNodeJBox2dEntity == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to acquire a new circle entity instance from PObjectManager"));
			return null;
		}

		lNodeJBox2dEntity.setBodyType(JBox2dEntityInstance.MAIN_BODY_NAME, Box2dBodyDefinition.BODY_TYPE_INDEX_STATIC);
		lNodeJBox2dEntity.loadPhysics(mWorld);

		if (lNodeJBox2dEntity.mainBody() == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to load the lItemJBox2dEntity physics"));
			return null;

		}

		lNodeJBox2dEntity.setAllFixturesCategory(CATEGORY_NODE); // what we are
		lNodeJBox2dEntity.setAllFixturesBitMask(CATEGORY_TRAIN); // what we collide width

		lNodeJBox2dEntity.setLinearVelocity(0, 0);
		lNodeJBox2dEntity.setAllFixtureFriction(1.0f);

		return lNodeJBox2dEntity;
	}

	public JBox2dEntityInstance getTrainCarriagePhysicsObject(JBox2dEntityInstance pLeadingTrain, float pWorldWidth, float pWorldHeight) {
		if (!isinitialized()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot retreive a new ObjectBox2dEntityInstance - Box2dWorldController is not initialized!");
			return null;

		}

		final var lResourceManager = mResourceController.resourceManager();

		var lObjectJBox2dEntity = lResourceManager.pobjectManager().getNewInstanceFromPObject(mWorld, "Train");// .getNewInstanceBoxInstance(mWorld, Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC, pWorldWidth,

		if (lObjectJBox2dEntity == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to acquire a new box entity instance from PObjectManager"));
			return null;
		}

		// pWorldHeight);

		// TODO: posiitoning
		final var lCarrigaPositionHolder = pLeadingTrain.getBodyByName("CarriageLocation");
		final float lUnitPositionX = lCarrigaPositionHolder.mBody.getPosition().x;
		final float lUnitPositionY = lCarrigaPositionHolder.mBody.getPosition().y;
		final float lToPixels = ConstantsPhysics.UnitsToPixels();

		lObjectJBox2dEntity.transformEntityInstance(lUnitPositionX * lToPixels, lUnitPositionY * lToPixels);
		lObjectJBox2dEntity.loadPhysics(mWorld);
		lObjectJBox2dEntity.setAllFixtureDensity(0.01f);
		lObjectJBox2dEntity.setAllBodiesAngularDamping(0.f);

//		lObjectJBox2dEntity.setAllFixturesCategory(CATEGORY_TRAIN); // what we are
//		lObjectJBox2dEntity.setAllFixturesBitMask(CATEGORY_NODE); // what we collide width

		if (lObjectJBox2dEntity.mainBody() == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to load the lItemJBox2dEntity physics"));
			return null;

		}

		return lObjectJBox2dEntity;

	}

	public JBox2dEntityInstance getTrainPhysicsObject(float pWorldPositionX, float pWorldPositionY, float pWorldWidth, float pWorldHeight) {
		if (!isinitialized()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot retreive a new ObjectBox2dEntityInstance - Box2dWorldController is not initialized!");
			return null;

		}

		final var lResourceManager = mResourceController.resourceManager();

		var lObjectJBox2dEntity = lResourceManager.pobjectManager().getNewInstanceFromPObject(mWorld, "Train");// .getNewInstanceBoxInstance(mWorld, Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC, pWorldWidth,
																												// pWorldHeight);

		if (lObjectJBox2dEntity == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to acquire a new box entity instance from PObjectManager"));
			return null;
		}

		lObjectJBox2dEntity.transformEntityInstance(pWorldPositionX, pWorldPositionY);
		lObjectJBox2dEntity.loadPhysics(mWorld);

		lObjectJBox2dEntity.setAllFixturesCategory(CATEGORY_TRAIN); // what we are
		lObjectJBox2dEntity.setAllFixturesBitMask(CATEGORY_NODE | CATEGORY_TRAIN); // what we collide width

		if (lObjectJBox2dEntity.mainBody() == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to load the lItemJBox2dEntity physics"));
			return null;

		}

		return lObjectJBox2dEntity;

	}

	public void connectTrainCarriages(Train pLead, Train pTail) {
		if (pLead == null || pLead.mRevJointPulling != null) {
			System.out.println("Cannot join carriages");
			return;
		}

		RevoluteJointDef lJointDef = new RevoluteJointDef();
		final var lAnchorBack = pLead.physicsObject().getBodyByName("carriageAnchorBack");
		final var lAnchorFront = pTail.physicsObject().getBodyByName("carriageAnchorFront");

		lJointDef.initialize(lAnchorBack.mBody, lAnchorFront.mBody, lAnchorBack.mBody.getWorldCenter());

		lJointDef.enableLimit = false;
		lJointDef.lowerAngle = (float) Math.toRadians(-20.f);
		lJointDef.upperAngle = (float) Math.toRadians(20.f);

		lJointDef.enableMotor = false;
		lJointDef.collideConnected = false;

		final var lRevJoint = (RevoluteJoint) mWorld.createJoint(lJointDef);
		pLead.mRevJointPulling = lRevJoint;

	}

	public PrismaticJoint getPrismaticJointToNode(Train pTrainToPull, Node pGoToNode, float pForce, float pSpeed) {
		PrismaticJointDef lJointDef = new PrismaticJointDef();

		final var lBodyA = pGoToNode.box2dEntityInstance().mainBody().mBody;
		final var lBodyB = pTrainToPull.physicsObject().mainBody().mBody;
		final var lAnchor = new Vec2(0, 0);
		final var lAxis = new Vec2(lBodyA.getWorldCenter().x - lBodyB.getWorldCenter().x, lBodyA.getWorldCenter().y - lBodyB.getWorldCenter().y); // [NORM] TODO:
		lAxis.normalize();

		lJointDef.collideConnected = true;
		lJointDef.referenceAngle = 0;

		lJointDef.initialize(lBodyA, lBodyB, lAnchor, lAxis);

		// FIXME: Enabling the prismatic joint limit causes the trains to launch into the atmosphere
		lJointDef.enableLimit = false;
		lJointDef.lowerTranslation = 0.f;

		final float lDist = Vector2f.distance(pTrainToPull.worldPositionX(), pTrainToPull.worldPositionY(), pGoToNode.worldPositionX, pGoToNode.worldPositionY);
		lJointDef.upperTranslation = ConstantsPhysics.toUnits(lDist);

		lJointDef.enableMotor = !pTrainToPull.isCarriage;
		lJointDef.maxMotorForce = pForce;
		lJointDef.motorSpeed = pSpeed;

		return (PrismaticJoint) mWorld.createJoint(lJointDef);

	}

}
