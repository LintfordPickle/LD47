package org.lintfordpickle.ld47.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class BackgroundScreen extends Screen {

	private Texture mIntroTexture;

	public BackgroundScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mShowInBackground = true;

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mIntroTexture = pResourceManager.textureManager().loadTexture("TEXTURE_INTRO", "res/textures/screens/textureScreenMenuBackground.png", entityGroupID());

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		final var lTextureBatch = mRendererManager.uiTextureBatch();

		final var lHudRect = pCore.HUD().boundingRectangle();

		final float lDestX = lHudRect.left();
		final float lDestY = lHudRect.top();
		final float lDestW = lHudRect.width();
		final float lDestH = lHudRect.height();

		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mIntroTexture, 0, 0, 800, 600, lDestX, lDestY, lDestW, lDestH, -.1f, 1, 1, 1, 1);
		lTextureBatch.end();
	}

}
