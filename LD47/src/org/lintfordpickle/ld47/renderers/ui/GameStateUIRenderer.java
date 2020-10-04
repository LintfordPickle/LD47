package org.lintfordpickle.ld47.renderers.ui;

import java.util.concurrent.TimeUnit;

import org.lintfordpickle.ld47.controllers.GameStateController;
import org.lintfordpickle.ld47.controllers.TrackController;
import org.lintfordpickle.ld47.controllers.TrainController;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.renderers.windows.components.UISlider;

public class GameStateUIRenderer extends UIWindow implements IProcessMouseInput {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "GameState UI Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private TrainController mTrainController;
	private TrackController mTrackController;
	private SpriteSheetDefinition mWorldDefinition;

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

	public GameStateUIRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mSpeedControlSlider = new UISlider(this);
		mSpeedControlSlider.buttonLabel("SPEED");
		mSpeedControlSlider.set(0, 0, 300, 30);
		mSpeedControlSlider.mMinValue = 1.f;
		mSpeedControlSlider.mMaxValue = 10.f;

		mComponents.add(mSpeedControlSlider);

		mIsOpen = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mGameStateController = (GameStateController) pCore.controllerManager().getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
		mTrackController = (TrackController) pCore.controllerManager().getControllerByNameRequired(TrackController.CONTROLLER_NAME, entityGroupID());
		mTrainController = (TrainController) pCore.controllerManager().getControllerByNameRequired(TrainController.CONTROLLER_NAME, entityGroupID());

		mSpeedControlSlider.initialize();
		mSpeedControlSlider.currentValue(1.5f);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mWorldDefinition = pResourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_WORLD", entityGroupID());

	}

	@Override
	public void update(LintfordCore pCore) {
		// Don't draw UI when game over
		if (mGameStateController.getHasLost() || mGameStateController.getHasWon())
			return;

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
		final var lTitleFont = mRendererManager.titleFont();
		lTitleFont.drawShadow(true);

		final TextureBatchPCT lTextureBatch = mRendererManager.uiTextureBatch();
		final var ll = mRendererManager.uiSpriteBatch();

		{ // Deco
			ll.begin(pCore.HUD());
			final var lTrainFrontSprite = mWorldDefinition.getSpriteFrame("TEXTURETRAINFRONT");
			final float lScale = 2.f;
			final float lWidth = lTrainFrontSprite.width() * lScale;

			ll.draw(mWorldDefinition, lTrainFrontSprite, -200.f - lWidth * .5f, lHudRect.top() + 15f, lTrainFrontSprite.width() * 2.f, lTrainFrontSprite.height() * 2.f, -0.1f, 1.f, 1.f, 1.f, 1.f);

			final var lTrainBackSprite = mWorldDefinition.getSpriteFrame("TEXTURETRAINBACK");
			ll.draw(mWorldDefinition, lTrainBackSprite, +200.f - lWidth * .5f, lHudRect.top() + 15f, lTrainBackSprite.width() * 2.f, lTrainBackSprite.height() * 2.f, -0.1f, 1.f, 1.f, 1.f, 1.f);
			ll.end();
		}

		{ // Time remaining

			final var lTextFont = mRendererManager.textFont();
			lTextFont.drawShadow(true);
			final var lTimeRemainingLabel = "TIME REMAINING";
			final var lLabelStringWidth = lTextFont.bitmap().getStringWidth(lTimeRemainingLabel, .6f);

			lTextFont.begin(pCore.HUD());
			lTextFont.draw(lTimeRemainingLabel, -lLabelStringWidth * .5f, lHudRect.top() + 45.f, -.01f, .6f);
			lTextFont.end();

			final long millis = (long) mGameStateController.gameState().timeRemaining;
			final var lTimeRemainingString = String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
					TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

			final var lHealthStringWidth = lTitleFont.bitmap().getStringWidth(lTimeRemainingString);

			lTitleFont.begin(pCore.HUD());
			lTitleFont.draw(lTimeRemainingString, -lHealthStringWidth * .5f, lHudRect.top() + 55.f, 1.f);
			lTitleFont.end();
		}

		{ // Health

			final var lHealthString = mGameStateController.getHealthStatus();;
			final var lHealthStringWidth = lTitleFont.bitmap().getStringWidth(lHealthString);

			lTitleFont.begin(pCore.HUD());
			lTitleFont.draw(lHealthString, -lHealthStringWidth * .5f, lHudRect.top() + 15.f, 1.f);
			lTitleFont.end();
		}

	}

}
