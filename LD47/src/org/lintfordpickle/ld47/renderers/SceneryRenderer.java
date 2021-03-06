package org.lintfordpickle.ld47.renderers;

import org.lintfordpickle.ld47.controllers.SceneryController;
import org.lintfordpickle.ld47.data.SceneryProp;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class SceneryRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Scenery Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneryController mSceneryController;
	private SpriteSheetDefinition mWorldSprites;
	private Texture mWorldTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 4;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneryRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mSceneryController = (SceneryController) pCore.controllerManager().getControllerByNameRequired(SceneryController.CONTROLLER_NAME, entityGroupID());

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
	public boolean handleInput(LintfordCore pCore) {
		if (!mSceneryController.isinitialized())
			return false;

		return super.handleInput(pCore);
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mSceneryController.isinitialized())
			return;

		final var lTextureBatch = mRendererManager.uiTextureBatch();

		final var lWorldScenery = mSceneryController.worldScenery();
		final var lItemsList = lWorldScenery.mSceneryItems;
		final int lNumItems = lItemsList.size();

		lTextureBatch.begin(pCore.gameCamera());

		for (int i = 0; i < lNumItems; i++) {
			final var lSceneryProp = lItemsList.get(i);

			drawProp(pCore, lTextureBatch, lSceneryProp);

		}

		if (mSceneryController.isInEditMode) {
			final int lCurrentEditPropIndex = mSceneryController.selectedItemIndex;
			final float lMouseWorldPosX = pCore.gameCamera().getMouseWorldSpaceX();
			final float lMouseWorldPosY = pCore.gameCamera().getMouseWorldSpaceY();

			drawPropGhost(pCore, lTextureBatch, lMouseWorldPosX, lMouseWorldPosY, SceneryController.getSelectedSpriteName(lCurrentEditPropIndex));

		}

		lTextureBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawProp(LintfordCore pCore, TextureBatchPCT pTextureBatch, SceneryProp pProp) {
		final var spriteFrame = mWorldSprites.getSpriteFrame(pProp.spriteItemName);
		pTextureBatch.draw(mWorldTexture, spriteFrame.x(), spriteFrame.y(), spriteFrame.w(), spriteFrame.h(), pProp.worldPositionX, pProp.worldPositionY, spriteFrame.w(), spriteFrame.h(), -0.1f, 1, 1, 1, 1);

	}

	private void drawPropGhost(LintfordCore pCore, TextureBatchPCT pTextureBatch, float pWorldX, float pWorldY, String pPropName) {
		final var spriteFrame = mWorldSprites.getSpriteFrame(pPropName);

		if (spriteFrame == null)
			return; // some sprites may have been removesd

		pTextureBatch.draw(mWorldTexture, spriteFrame.x(), spriteFrame.y(), spriteFrame.w(), spriteFrame.h(), pWorldX, pWorldY, spriteFrame.w(), spriteFrame.h(), -0.1f, .2f, .2f, .2f, 0.5f);

	}

}
