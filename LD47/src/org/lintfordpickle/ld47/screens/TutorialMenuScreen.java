package org.lintfordpickle.ld47.screens;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;

public class TutorialMenuScreen extends MenuScreen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Texture mTutorialTexture;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TutorialMenuScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "");

		mIsPopup = true;
		mShowInBackground = false;

		// Don't allow game to continue while waiting for this screen to finish
		mBlockInputInBackground = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTutorialTexture = pResourceManager.textureManager().loadTexture("TEXTURE_TUTORIAL", "res/textures/screens/textureScreenTutorial.png", entityGroupID());

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_SPACE) || pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE) || pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER)
				|| pCore.input().mouse().isMouseLeftButtonDown()) {

			mScreenManager.uiSounds().play("SOUND_MENU_CLICK");

			exitScreen();

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		pCore.gameTime().setPaused(mScreenState == ScreenState.Active);

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		final var lTextureWidth = mTutorialTexture.getTextureWidth();
		final var lTextureHeight = mTutorialTexture.getTextureHeight();

		final var lTextureBatch = mRendererManager.uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mTutorialTexture, 0, 0, lTextureWidth, lTextureHeight, -lTextureWidth * .5f, -lTextureHeight * .5f, lTextureWidth, lTextureHeight, -0.001f, 1f, 1f, 1f, 1f);
		lTextureBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		// handled in handleInput (click to leave)

	}

}
