package org.lintfordpickle.ld47.renderers.ui;

import org.lintfordpickle.ld47.controllers.CameraMovementController;
import org.lintfordpickle.ld47.controllers.GameStateController;
import org.lintfordpickle.ld47.controllers.TrackController;
import org.lintfordpickle.ld47.controllers.TrainController;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.renderers.windows.components.UIIconButton;
import net.lintford.library.renderers.windows.components.UISlider;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class PlayerControlsRenderer extends UIWindow implements IProcessMouseInput, EntryInteractions {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Player Controls Renderer";

	private static final int BUTTON_ID_FOLLOW_CAMERA = 0;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SpriteSheetDefinition mWorldSpriteSheet;
	private GameStateController mGameStateController;
	private CameraMovementController mCameraMovementController;
	private TrainController mTrainController;
	private TrackController mTrackController;

	private UIIconButton mFollowTrainToggleButton;
	private UISlider mSpeedControlSlider;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return false;
	}

	float mLeftMouseCooldownTimer;

	public boolean isCoolDownElapsed() {
		return mLeftMouseCooldownTimer <= 0.f;
	}

	public void resetCoolDownTimer() {
		mLeftMouseCooldownTimer = 200.f;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlayerControlsRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mSpeedControlSlider = new UISlider(this);
		mSpeedControlSlider.buttonLabel("SPEED");
		mSpeedControlSlider.set(0, 0, 300, 30);
		mSpeedControlSlider.mMinValue = 1.f;
		mSpeedControlSlider.mMaxValue = 10.f;

		mFollowTrainToggleButton = new UIIconButton(this);
		mFollowTrainToggleButton.set(0, 0, 64, 64);
		mFollowTrainToggleButton.setClickListener(this, BUTTON_ID_FOLLOW_CAMERA);

		mComponents.add(mFollowTrainToggleButton);
		mComponents.add(mSpeedControlSlider);

		mIsOpen = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mGameStateController = (GameStateController) pCore.controllerManager().getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
		mCameraMovementController = (CameraMovementController) pCore.controllerManager().getControllerByNameRequired(CameraMovementController.CONTROLLER_NAME, entityGroupID());
		mTrackController = (TrackController) pCore.controllerManager().getControllerByNameRequired(TrackController.CONTROLLER_NAME, entityGroupID());
		mTrainController = (TrainController) pCore.controllerManager().getControllerByNameRequired(TrainController.CONTROLLER_NAME, entityGroupID());

		mSpeedControlSlider.initialize();
		mSpeedControlSlider.currentValue(1.5f);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mWorldSpriteSheet = pResourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_WORLD", entityGroupID());

		final var lButtonRect = mWorldSpriteSheet.getSpriteFrame("TEXTUREBUTTONFOLLOW");
		mFollowTrainToggleButton.setTextureSource(mWorldSpriteSheet.texture(), lButtonRect.x(), lButtonRect.y(), lButtonRect.w(), lButtonRect.h());

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		return mFollowTrainToggleButton.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		// Don't draw UI when game over
		if (mGameStateController.getHasLost() || mGameStateController.getHasWon())
			return;

		final var lHudRect = pCore.HUD().boundingRectangle();

		mFollowTrainToggleButton.set(lHudRect.right() - 64.f - 15.f, lHudRect.bottom() - 15.f - 64.f, 64, 64);
		mFollowTrainToggleButton.update(pCore);

		final float lSpeedAsPerSlider = mSpeedControlSlider.currentValue();

		final var lPlayerTrain = mTrainController.mainTrain();
		if (lPlayerTrain != null) {
			lPlayerTrain.setSpeed(lSpeedAsPerSlider);

		}
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mTrackController.isinitialized())
			return;

		// Don't draw UI when game over
		if (mGameStateController.getHasLost() || mGameStateController.getHasWon())
			return;

		final var lHudRect = pCore.HUD().boundingRectangle();

		final TextureBatchPCT lTextureBatch = mRendererManager.uiTextureBatch();

		final float lScreenEdgePadding = 15.f;
		final float lSpeedControlWidth = 400;
		final float lSpeedControlHeight = 200;

		mSpeedControlSlider.set(0, 0, 400, 200);
		mSpeedControlSlider.set(-lSpeedControlWidth * .5f, lHudRect.bottom() - lSpeedControlHeight - lScreenEdgePadding, lSpeedControlWidth, lSpeedControlHeight);

		{
			final var lHandleSprite = mWorldSpriteSheet.getSpriteFrame("TEXTURECONTROLHANDLE");
			final var lHandleOuterSprite = mWorldSpriteSheet.getSpriteFrame("TEXTURECONTROLHANDLEOUTER");

			final float lDestX = 0.f;
			final float lDestY = lHudRect.bottom() - 45.f;

			final float lSrcX = lHandleSprite.x();
			final float lSrcY = lHandleSprite.y();
			final float lSrcW = lHandleSprite.w();
			final float lSrcH = lHandleSprite.h();

			final float lScale = 1.2f;

			final float lMinAngleInRadians = (float) Math.toRadians(10.f);
			final float lMaxAngleInRadians = (float) Math.PI - (float) Math.toRadians(10.f);
			;
			float test = MathHelper.scaleToRange(mSpeedControlSlider.currentValue(), mSpeedControlSlider.mMinValue, mSpeedControlSlider.mMaxValue, lMinAngleInRadians, lMaxAngleInRadians);

			final float lRotationsInRadians = test - (float) Math.toRadians(90.f); // (float) pCore.appTime().totalTimeMilli() * 0.001f;

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.drawAroundCenter(mWorldSpriteSheet.texture(), lHandleOuterSprite.x(), lHandleOuterSprite.y(), lHandleOuterSprite.w(), lHandleOuterSprite.h(), lDestX, lDestY, lSrcW, lSrcH, -0.01f, 0.f, 0.f, 0.f,
					lScale, 1, 1, 1, 1);
			lTextureBatch.drawAroundCenter(mWorldSpriteSheet.texture(), lSrcX, lSrcY, lSrcW, lSrcH, lDestX, lDestY, lSrcW, lSrcH, -0.01f, lRotationsInRadians, 0.f, 0.f, lScale, 1, 1, 1, 1);
			lTextureBatch.end();

		}

		{
			mFollowTrainToggleButton.draw(pCore, mRendererManager.uiTextureBatch(), mWorldSpriteSheet.texture(), mRendererManager.textFont(), -0.1f);
		}

	}

	@Override
	public void menuEntryOnClick(InputManager pInputState, int pEntryID) {
		if (pEntryID == BUTTON_ID_FOLLOW_CAMERA) {
			if (mCameraMovementController.isFollowingTrain()) {
				mCameraMovementController.setFollowTrain(null);

			} else {
				final var lMainTrain = mTrainController.mainTrain();
				mCameraMovementController.setFollowTrain(lMainTrain);

			}

		}

	}

}
