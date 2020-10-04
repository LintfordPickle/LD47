package org.lintfordpickle.ld47.data.train;

import org.jbox2d.dynamics.joints.Joint;
import org.lintfordpickle.ld47.data.track.Edge;
import org.lintfordpickle.ld47.data.track.Node;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.entity.PooledBaseData;
import net.lintford.library.core.maths.MathHelper;

public class Train extends PooledBaseData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 4609684006738847793L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public boolean isCarriage;
	private boolean mHasHadCollision;
	private float mTimeSinceCollision;

	private boolean mIsAssigned;
	private JBox2dEntityInstance mBox2dEntityInstance;

	private boolean mPrisJointDestroyed;
	private Joint mPrisJoint;
	public Joint mRevJointPulling;

	private float mWorldPositionX;
	private float mWorldPositionY;
	private Edge mCurrentEdge;
	private Node mDestinationNode;
	private int mHasArrivedUid = -1;
	private boolean mIsPlayerControlled;

	private float mForce;
	private float mTargetSpeed;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public float getSpeed() {
		return mTargetSpeed;
	}

	public void setSpeed(float pNewSpeed) {
		final float MIN_SPEED = 0.5f;
		mTargetSpeed = MathHelper.clamp(pNewSpeed, MIN_SPEED, pNewSpeed);
	}

	public float getForce() {
		return mForce;
	}

	public boolean wasPrisJointDestroyed() {
		return mPrisJointDestroyed;
	}

	public void wasPrisJointDestroyed(boolean pNewValue) {
		mPrisJointDestroyed = pNewValue;
	}

	public boolean isPlayerControlled() {
		return mIsPlayerControlled;
	}

	public void isPlayerControlled(boolean pNewValue) {
		if (pNewValue) {
			mForce = 5.0f;
		}

		mIsPlayerControlled = pNewValue;
	}

	public void setHasHadCollision() {
		if (!mHasHadCollision) { // don't keep resetting the timer on collisions, or trains never leave
			mHasHadCollision = true;
			mTimeSinceCollision = 0.f;

		}
	}

	public boolean hasHadCollision() {
		return mHasHadCollision;
	}

	public float timeSinceCollision() {
		return mTimeSinceCollision;
	}

	public Joint pullJoint() {
		return mPrisJoint;
	}

	public Edge currentEdge() {
		return mCurrentEdge;
	}

	public Node destinationNode() {
		return mDestinationNode;
	}

	public float worldPositionX() {
		return mWorldPositionX;
	}

	public float worldPositionY() {
		return mWorldPositionY;
	}

	public JBox2dEntityInstance physicsObject() {
		return mBox2dEntityInstance;
	}

	public boolean isInMotion() {
		return mPrisJoint != null;
	}

	public boolean hasPhysicsObject() {
		return physicsObject() != null;
	}

	@Override
	public boolean isAssigned() {
		return mIsAssigned;
	}

	public boolean hasArrived() {
		return mDestinationNode != null && mDestinationNode.poolUid == mHasArrivedUid;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Train(int pPoolUid) {
		super(pPoolUid);
		/**
		 * 
		 * Lame = .4 / .9 Sane = 1.5 / 5 Crazy = 3.8 / 30
		 * 
		 */

		mForce = 1.5f;
		mTargetSpeed = 5.f;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void update(LintfordCore pCore) {
		// as soon as we have a physics body, update the position of this instance to match the box2d world's position
		if (hasPhysicsObject()) {
			final var lMainBody = mBox2dEntityInstance.mainBody();
			mWorldPositionX = ConstantsPhysics.toPixels(lMainBody.mBody.getPosition().x);
			mWorldPositionY = ConstantsPhysics.toPixels(lMainBody.mBody.getPosition().y);

		}

		//
		if (isInMotion()) {
			// Check

		}

		if (hasHadCollision()) {
			mTimeSinceCollision += pCore.gameTime().elapsedTimeMilli();

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void setPhysicsObject(JBox2dEntityInstance pPhysicsObject) {
		mBox2dEntityInstance = pPhysicsObject;

	}

	public void setDestination(Joint pPrisJoint, Edge pGoAlongEdge, Node pDestinationNode, boolean pFirstDestination) {
		mPrisJoint = pPrisJoint;
		mPrisJointDestroyed = pPrisJoint == null;
		mCurrentEdge = pGoAlongEdge;
		mDestinationNode = pDestinationNode;
		mHasArrivedUid = -1;

		updatePhysicsPosition(pFirstDestination);

	}

	public boolean hasDestination() {
		return mDestinationNode != null;
	}

	public void setLocation(Node pNode, boolean pFirstTime) {
		if (pNode == null)
			return;
		mDestinationNode = pNode;

		mWorldPositionX = pNode.worldPositionX;
		mWorldPositionY = pNode.worldPositionY;

		updatePhysicsPosition(true);

	}

	public void updatePhysicsPosition(boolean pFirstTime) {
		if (!hasPhysicsObject())
			return;

		float lAngle = 0.f;
		if (hasDestination()) {
			final var lVectorX = mDestinationNode.worldPositionX - mWorldPositionX;
			final var lVectorY = mDestinationNode.worldPositionY - mWorldPositionY;

			lAngle = (float) Math.atan2(lVectorY, lVectorX);

		}

		mBox2dEntityInstance.transformEntityInstance(mWorldPositionX, mWorldPositionY, lAngle);

		// Update carriage positions ?

	}

	public void setHasArrived(int pArrivedAtNodeUid) {
		if (mDestinationNode == null)
			return; // has no destination yet

		if (pArrivedAtNodeUid != mDestinationNode.poolUid)
			return; // only interested in the actual destination node

		mHasArrivedUid = pArrivedAtNodeUid;

	}

	public void cleanUp() {
		mCurrentEdge = null;
		mDestinationNode = null;
		mPrisJoint = null;
		mRevJointPulling = null;
		mHasHadCollision = false;
		mTimeSinceCollision = 0.f;

	}

}
