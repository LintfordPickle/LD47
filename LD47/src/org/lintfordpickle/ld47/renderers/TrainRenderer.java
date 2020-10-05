package org.lintfordpickle.ld47.renderers;

import org.lintfordpickle.ld47.controllers.TrainController;
import org.lintfordpickle.ld47.data.train.Train;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class TrainRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Train Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TrainController mTrainController;
	private SpriteSheetDefinition mWorldSprites;
	private Texture mWorldTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 3;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrainRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mTrainController = (TrainController) pCore.controllerManager().getControllerByNameRequired(TrainController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mWorldSprites = pResourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_WORLD", entityGroupID());
		mWorldTexture = mWorldSprites.texture();

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mWorldSprites = null;
		mWorldTexture = null;

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mTrainController.isinitialized())
			return;

		final var lTrainManager = mTrainController.trainManager();
		final var lActiveTrains = lTrainManager.activeTrains();
		final int lNumTrains = lActiveTrains.size();

		for (int i = 0; i < lNumTrains; i++) {
			final var lTrain = lActiveTrains.get(i);

			if (!lTrain.hasPhysicsObject())
				continue;

			drawTrain(pCore, lTrain);

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawTrain(LintfordCore pCore, Train pTrain) {
		final var lTextureBatch = mRendererManager.uiTextureBatch();
		
		final var lTrainSpriteSrcRect = pTrain.isPlayerControlled() ? mWorldSprites.getSpriteFrame("TEXTURETRAIN01") : mWorldSprites.getSpriteFrame("TEXTURETRAIN00");

		final var lWorldPosX = pTrain.worldPositionX();
		final var lWorldPosY = pTrain.worldPositionY();

		float lAngle = pTrain.physicsObject().mainBody().mBody.getAngle() + (float) Math.toRadians(90.f);

		final var lR = pTrain.hasHadCollision() ? .8f : pTrain.isPlayerControlled() ? 1.f : .14f;
		final var lG = pTrain.hasHadCollision() ? .1f : pTrain.isPlayerControlled() ? 1.f : .94f;
		final var lB = pTrain.hasHadCollision() ? .1f : pTrain.isPlayerControlled() ? 1.f : .94f;

		final float lWidth = lTrainSpriteSrcRect.w();
		final float lHeight = lTrainSpriteSrcRect.h();
		
		lTextureBatch.begin(pCore.gameCamera());
		lTextureBatch.drawAroundCenter(mWorldTexture, 
				lTrainSpriteSrcRect.x(), lTrainSpriteSrcRect.y(), lTrainSpriteSrcRect.w(), lTrainSpriteSrcRect.h(), 
				lWorldPosX, lWorldPosY, lWidth, lHeight, -0.01f, lAngle, 0.f, 0.f, 1.f,
				lR, lG, lB, 1);
		lTextureBatch.end();

	}

}
