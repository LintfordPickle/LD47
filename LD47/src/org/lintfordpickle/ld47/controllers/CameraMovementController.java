package org.lintfordpickle.ld47.controllers;

import org.lintfordpickle.ld47.GameConstants;
import org.lintfordpickle.ld47.data.train.Train;
import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Vector2f;

public class CameraMovementController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Camera Movement Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 40.f;
	private static final float CAMERA_MAN_MOVE_SPEED_MAX = 10f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Rectangle mPlayArea;
	private ICamera mGameCamera;
	private Vector2f mVelocity;
	private Train mFollowTrain;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isFollowingTrain() {
		return mFollowTrain != null;
	}

	public void setFollowTrain(Train pFollowTrain) {
		mFollowTrain = pFollowTrain;

	}

	public Rectangle playArea() {
		return mPlayArea;
	}

	public void setPlayArea(float pX, float pY, float pWidth, float pHeight) {
		mPlayArea.set(pX, pY, pWidth, pHeight);
	}

	public ICamera gameCamera() {
		return mGameCamera;
	}

	@Override
	public boolean isinitialized() {
		return mGameCamera != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraMovementController(ControllerManager pControllerManager, ICamera pCamera, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mVelocity = new Vector2f();
		mPlayArea = new Rectangle();

		//
		mGameCamera = pCamera;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mGameCamera == null)
			return false;

		final float lElapsed = (float) pCore.appTime().elapsedTimeMilli() * 0.001f;
		final float lOneOverCameraZoom = mGameCamera.getZoomFactorOverOne();
		final float speed = CAMERA_MAN_MOVE_SPEED * lOneOverCameraZoom;

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			return false; // editor controls

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
			mVelocity.x += speed * lElapsed;
			mFollowTrain = null; // stop auto follow

		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
			mVelocity.x -= speed * lElapsed;
			mFollowTrain = null; // stop auto follow

		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
			mVelocity.y -= speed * lElapsed;
			mFollowTrain = null; // stop auto follow

		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
			mVelocity.y += speed * lElapsed;
			mFollowTrain = null; // stop auto follow

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		if (mFollowTrain != null) {
			mGameCamera.setPosition(-mFollowTrain.worldPositionX(), -mFollowTrain.worldPositionY());

		} else {
			// Cap
			if (mVelocity.x < -CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.x = -CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.x > CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.x = CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.y < -CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.y = -CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.y > CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.y = CAMERA_MAN_MOVE_SPEED_MAX;

			float elapsed = (float) pCore.appTime().elapsedTimeMilli();

			// Apply
			float lCurX = mGameCamera.getPosition().x;
			float lCurY = mGameCamera.getPosition().y;

			if (!GameConstants.CAMERA_DEBUG_MODE && mPlayArea != null && !mPlayArea.isEmpty()) {
				if (lCurX < mPlayArea.left()) {
					lCurX = mPlayArea.left();
					mVelocity.x = 0.f;
				}

				if (lCurX > mPlayArea.right()) {
					lCurX = mPlayArea.right();
					mVelocity.x = 0.f;
				}

				if (lCurY < mPlayArea.top()) {
					lCurY = mPlayArea.top();
					mVelocity.y = 0.f;
				}

				if (lCurY > mPlayArea.bottom()) {
					lCurY = mPlayArea.bottom();
					mVelocity.y = 0.f;
				}

			}
			mGameCamera.setPosition(lCurX + mVelocity.x * elapsed, lCurY + mVelocity.y * elapsed);

		}

		// DRAG
		mVelocity.x *= 0.857f;
		mVelocity.y *= 0.857f;

		// There are minimums for the camera

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float pZoomFactor) {
		mGameCamera.setZoomFactor(pZoomFactor);

	}

}
