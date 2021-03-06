package org.lintfordpickle.ld47.renderers.ui;

import java.util.concurrent.TimeUnit;

import org.lintfordpickle.ld47.controllers.GameStateController;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.windows.UIWindow;

public class GameStateUIRenderer extends UIWindow implements IProcessMouseInput {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "GameState UI Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private SpriteSheetDefinition mWorldDefinition;

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

		mIsOpen = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mGameStateController = (GameStateController) pCore.controllerManager().getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mWorldDefinition = pResourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_WORLD", entityGroupID());

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mGameStateController.isinitialized())
			return;

		// Don't draw UI when game over
		if (mGameStateController.getHasLost() || mGameStateController.getHasWon())
			return;

		final var lHudRect = pCore.HUD().boundingRectangle();
		final var lTitleFont = mRendererManager.titleFont();
		lTitleFont.drawShadow(true);

		final var lSpriteBatch = mRendererManager.uiSpriteBatch();

		{

			lSpriteBatch.begin(pCore.HUD());
			final var lTrainFrontSprite = mWorldDefinition.getSpriteFrame("TEXTURETOPPANEL");
			final float lScale = 1.5f;
			final float lWidth = lTrainFrontSprite.width();
			final float lHeight = lTrainFrontSprite.height();

			lSpriteBatch.draw(mWorldDefinition, lTrainFrontSprite, lHudRect.centerX() - lWidth * .5f * lScale, lHudRect.top() - lHeight * .5f, lTrainFrontSprite.width() * lScale, lTrainFrontSprite.height() * lScale,
					-0.1f, 1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.end();

		}

		{ // Deco
			lSpriteBatch.begin(pCore.HUD());
			final var lTrainFrontSprite = mWorldDefinition.getSpriteFrame("TEXTURETRAINFRONT");
			final float lScale = 2.f;
			final float lWidth = lTrainFrontSprite.width() * lScale;

			lSpriteBatch.draw(mWorldDefinition, lTrainFrontSprite, -200.f - lWidth * .5f, lHudRect.top() + 15f, lTrainFrontSprite.width() * 2.f, lTrainFrontSprite.height() * 2.f, -0.1f, 1.f, 1.f, 1.f, 1.f);

			final var lTrainBackSprite = mWorldDefinition.getSpriteFrame("TEXTURETRAINBACK");
			lSpriteBatch.draw(mWorldDefinition, lTrainBackSprite, +200.f - lWidth * .5f, lHudRect.top() + 15f, lTrainBackSprite.width() * 2.f, lTrainBackSprite.height() * 2.f, -0.1f, 1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.end();
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

			final var lHealthString = mGameStateController.getHealthStatus();
			;
			final var lHealthStringWidth = lTitleFont.bitmap().getStringWidth(lHealthString);

			lTitleFont.begin(pCore.HUD());
			lTitleFont.draw(lHealthString, -lHealthStringWidth * .5f, lHudRect.top() + 15.f, 1.f);
			lTitleFont.end();
		}

	}

}
