package org.lintfordpickle.ld47.renderers;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.geometry.FullScreenTexturedQuad;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class BackgroundRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Background Grass Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Texture mWorldTexture;
	private FullScreenTexturedQuad mFullScreenTexturedQuad;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 1;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BackgroundRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mFullScreenTexturedQuad = new FullScreenTexturedQuad();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mWorldTexture = pResourceManager.textureManager().loadTexture("TEXTURE_GRASS", "res/textures/textureGrass00.png", GL11.GL_NEAREST, entityGroupID());

		mFullScreenTexturedQuad.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mWorldTexture = null;

		mFullScreenTexturedQuad.unloadGLContent();

	}

	@Override
	public void draw(LintfordCore pCore) {

		final var mTextureBatch = mRendererManager.uiTextureBatch();

		final float lScale = 3.f;
		DisplayManager lDisplay = pCore.config().display();
		final var lX = -lDisplay.windowWidth() * .5f * lScale;
		final var lY = -lDisplay.windowHeight() * .5f * lScale;
		final var lW = lDisplay.windowWidth() * lScale;
		final var lH = lDisplay.windowHeight() * lScale;

		mTextureBatch.begin(pCore.gameCamera());
		mTextureBatch.draw(mWorldTexture, lX, lY, lW, lH, lX, lY, lW, lH, -.01f, 1f, 1f, 1f, 1f);
		mTextureBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
