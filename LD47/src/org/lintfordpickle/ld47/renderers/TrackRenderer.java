package org.lintfordpickle.ld47.renderers;

import org.jbox2d.common.Vec2;
import org.lintfordpickle.ld47.controllers.TrackController;
import org.lintfordpickle.ld47.data.track.Edge;
import org.lintfordpickle.ld47.data.track.Node;
import org.lintfordpickle.ld47.data.track.Track;
import org.lintfordpickle.ld47.graphics.TrackBatchPT;
import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.audio.AudioFireAndForgetManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class TrackRenderer extends BaseRenderer implements IProcessMouseInput {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Track Renderer";

	private static final Vec2 TempTrackVec2 = new Vec2();

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private AudioFireAndForgetManager mTrainSoundManager;

	private TrackController mTrackController;
	private Texture mTrackTexture;
	private SpriteSheetDefinition mWorldSpriteSheet; // signals

	private TrackBatchPT mTrackBatchPCT;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 2;
	}

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

	public TrackRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mTrackBatchPCT = new TrackBatchPT();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mTrackController = (TrackController) pCore.controllerManager().getControllerByNameRequired(TrackController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTrackTexture = pResourceManager.textureManager().loadTexture("TEXTURE_TRACK", "res/textures/textureTrack.png", GL11.GL_NEAREST, entityGroupID());
		mWorldSpriteSheet = pResourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_WORLD", entityGroupID());

		mTrackBatchPCT.loadGLContent(pResourceManager);

		pResourceManager.audioManager().loadAudioFile("SOUND_SIGNAL_CHANGE", "res/sounds/soundSignalChange.wav", false);

		mTrainSoundManager = new AudioFireAndForgetManager(pResourceManager.audioManager());
		mTrainSoundManager.acquireAudioSources(2);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTrackBatchPCT.unloadGLContent();
		mWorldSpriteSheet = null;

		mTrainSoundManager.unassign();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		mLeftMouseCooldownTimer -= pCore.appTime().elapsedTimeMilli();

		final float lMouseWorldSpaceX = pCore.gameCamera().getMouseWorldSpaceX();
		final float lMouseWorldSpaceY = pCore.gameCamera().getMouseWorldSpaceY();

		if (pCore.input().mouse().isMouseLeftButtonDownTimed(this)) {
			final var lTrack = mTrackController.track();

			final int lEdgeCount = lTrack.edges.size();

			for (int i = 0; i < lEdgeCount; i++) {
				final var lEdge = lTrack.edges.get(i);
				if (lEdge != null && lEdge.signalNode != null && lEdge.signalNode.isSignalActive) {
					final var lSignalNode = lTrack.getNodeByUid(lEdge.signalNode.signalNodeUid);

					final var lBoxPosX = lSignalNode.worldPositionX + lEdge.signalNode.signalBoxOffsetX;
					final var lBoxPosY = lSignalNode.worldPositionY + lEdge.signalNode.signalBoxOffsetY;

					if (lEdge != null && Vector2f.distance(lMouseWorldSpaceX, lMouseWorldSpaceY, lBoxPosX, lBoxPosY) < 10.f) {
						lEdge.signalNode.toggleSignal();

						final var lBoxWorldPositionX = lSignalNode.worldPositionX;
						final var lBoxWorldPositionY = lSignalNode.worldPositionY;

						mTrainSoundManager.play("SOUND_SIGNAL_CHANGE", lBoxWorldPositionX, lBoxWorldPositionY, 0.f, 0.f);

					}

				}

			}

		}

		return super.handleInput(pCore);
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mTrackController.isinitialized())
			return;

		final var lTrack = mTrackController.track();
		drawTrack(pCore, lTrack);

	}

	// ---------------------------------------------aw
	// Methods
	// ---------------------------------------------

	private void drawTrack(LintfordCore pCore, Track pTrack) {
		final var lEdgeList = pTrack.edges;

		final var lSignalBatch = mRendererManager.uiTextureBatch();

		lSignalBatch.begin(pCore.gameCamera());
		mTrackBatchPCT.begin(pCore.gameCamera());

		final var lEdgeCount = lEdgeList.size();
		for (int i = 0; i < lEdgeCount; i++) {
			final var lEdge = lEdgeList.get(i);

			final var lNodeA = pTrack.getNodeByUid(lEdge.nodeAUid);
			final var lNodeB = pTrack.getNodeByUid(lEdge.nodeBUid);

			drawTrackSegment(pCore, mTrackBatchPCT, lNodeA, lNodeB);

			if (lEdge.signalNode != null && lEdge.signalNode.isSignalActive) {
				drawSignalBox(pCore, lSignalBatch, pTrack, lEdge, lNodeA);

			}

		}

		mTrackBatchPCT.end();
		lSignalBatch.end();

	}

	private void drawTrackSegment(LintfordCore pCore, TrackBatchPT pTextureBatch, Node pTrackNodeA, Node pTrackNodeB) {
		final var lSegmentLength = Vector2f.distance(pTrackNodeA.worldPositionX, pTrackNodeA.worldPositionY, pTrackNodeB.worldPositionX, pTrackNodeB.worldPositionY);

		final var lRotX = -0.f;
		final var lRotY = -lSegmentLength * .5f;// lSegmentLength;
		final var lSegmentAngle = (float) Math.atan2(pTrackNodeB.worldPositionY - pTrackNodeA.worldPositionY, pTrackNodeB.worldPositionX - pTrackNodeA.worldPositionX) + (float) Math.toRadians(-90);

		// Variable seg length
		final float lPosX = pTrackNodeA.worldPositionX;
		final float lPosY = pTrackNodeA.worldPositionY;
		final float lPosW = 16.f;
		final float lPosH = lSegmentLength;

		final float lSrcX = 0;
		final float lSrcY = 0;
		final float lSrcW = 16.f;
		final float lSrcH = 32.f;

		// WOPRLD_TEXTURE
		pTextureBatch.drawAroundCenter(mTrackTexture, lSrcX, lSrcY, lSrcW, lSrcH, lPosX, lPosY, lPosW, lPosH, -0.1f, lSegmentAngle, lRotX, lRotY, lSegmentLength);

	}

	private void drawSignalBox(LintfordCore pCore, TextureBatchPCT pTextureBatch, Track pTrack, Edge pActiveEdge, Node pTrackNode) {
		final var lIsLeftSignalActive = pActiveEdge.signalNode.leftEnabled;
		final var lActiveEdgeUid = lIsLeftSignalActive ? pActiveEdge.signalNode.leftEdgeUid : pActiveEdge.signalNode.rightEdgeUid;
		final var lActiveEdge = pTrack.getEdgeByUid(lActiveEdgeUid);

		final int pCommonNodeUid = Edge.getCommonNodeUid(pActiveEdge, lActiveEdge);

		final var lActiveNode = pTrack.getNodeByUid(pCommonNodeUid);
		final var lOtherNodeUid = lActiveEdge.getOtherNodeUid(lActiveNode.poolUid);
		final var lOtherNode = pTrack.getNodeByUid(lOtherNodeUid);
		final float lVectorX = lOtherNode.worldPositionX - lActiveNode.worldPositionX;
		final float lVectorY = lOtherNode.worldPositionY - lActiveNode.worldPositionY;

		TempTrackVec2.set(lVectorX, lVectorY);
		TempTrackVec2.normalize();

		final var lWorldTexture = mWorldSpriteSheet.texture();

		final var lSignalArrow = mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALARROW");
		final var lSignalArrowAngle = (float) Math.atan2(TempTrackVec2.y, TempTrackVec2.x) + (float) Math.toRadians(90.f);

		{

			final float lSrcX = lSignalArrow.x();
			final float lSrcY = lSignalArrow.y();
			final float lSrcW = lSignalArrow.w();
			final float lSrcH = lSignalArrow.h();

			pTextureBatch.drawAroundCenter(lWorldTexture, lSrcX, lSrcY, lSrcW, lSrcH, lActiveNode.worldPositionX, lActiveNode.worldPositionY, lSrcW, lSrcH, -0.1f, lSignalArrowAngle, .0f, lSrcH * .5f, 1.f, 1, 1, 1, 1);
		}

		{ // signal lamp

			final float lLampOffsetX = pActiveEdge.signalNode.signalLampOffsetX;
			final float lLampOffsetY = pActiveEdge.signalNode.signalLampOffsetY;

			final var lSignalBounds = lIsLeftSignalActive ? mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALLEFT") : mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALRIGHT");

			final float lLampWidth = lSignalBounds.width();
			final float lLampHeight = lSignalBounds.height();

			pTextureBatch.draw(lWorldTexture, lSignalBounds, lActiveNode.worldPositionX - 16.f + lLampOffsetX, lActiveNode.worldPositionY - 32.f + lLampOffsetY, lLampWidth, lLampHeight, -0.1f, 1, 1, 1, 1);

		}

		{ // signal box (clickable bit)

			final float lBoxOffsetX = pActiveEdge.signalNode.signalBoxOffsetX;
			final float lBoxOffsetY = pActiveEdge.signalNode.signalBoxOffsetY;

			final var lSignalBounds = mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALBOX");

			final float lBoxWidth = lSignalBounds.width();
			final float lBoxHeight = lSignalBounds.height();

			pTextureBatch.draw(lWorldTexture, lSignalBounds, lActiveNode.worldPositionX - lBoxWidth * .5f + lBoxOffsetX, lActiveNode.worldPositionY - lBoxHeight * .5f + lBoxOffsetY, lBoxWidth, lBoxHeight, -0.1f, 1, 1, 1,
					1);

		}

	}

	public void debugDrawNodes(LintfordCore pCore) {
		final var lTrack = mTrackController.track();
		final var lNodeList = lTrack.nodes;

		GL11.glPointSize(4.f);

		final var lNodeCount = lNodeList.size();
		for (int i = 0; i < lNodeCount; i++) {
			final var lNode = lNodeList.get(i);

			Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lNode.worldPositionX, lNode.worldPositionY, -0.01f, 1.f, 0.f, 0.f, 1.f);

		}

	}

	public void debugDrawEdges(LintfordCore pCore) {

		final var lTrack = mTrackController.track();
		final var lEdgeList = lTrack.edges;

		Debug.debugManager().drawers().beginLineRenderer(pCore.gameCamera(), GL11.GL_LINES, 2.f);

		final var lEdgeCount = lEdgeList.size();
		for (int i = 0; i < lEdgeCount; i++) {
			final var lEdge = lEdgeList.get(i);

			final var lNodeA = lTrack.getNodeByUid(lEdge.nodeAUid);
			final var lNodeB = lTrack.getNodeByUid(lEdge.nodeBUid);

			Debug.debugManager().drawers().drawLine(lNodeA.worldPositionX, lNodeA.worldPositionY, lNodeB.worldPositionX, lNodeB.worldPositionY);

			if (lEdge.signalNode != null && lEdge.signalNode.isSignalActive) {
				final var lActiveEdgeUid = lEdge.signalNode.leftEnabled ? lEdge.signalNode.leftEdgeUid : lEdge.signalNode.rightEdgeUid;
				final var lActiveEdge = lTrack.getEdgeByUid(lActiveEdgeUid);

				final int pCommonNodeUid = Edge.getCommonNodeUid(lEdge, lActiveEdge);

				final var lActiveNode = lTrack.getNodeByUid(pCommonNodeUid);
				final var lOtherNodeUid = lActiveEdge.getOtherNodeUid(lActiveNode.poolUid);
				final var lOtherNode = lTrack.getNodeByUid(lOtherNodeUid);
				final float lVectorX = lOtherNode.worldPositionX - lActiveNode.worldPositionX;
				final float lVectorY = lOtherNode.worldPositionY - lActiveNode.worldPositionY;

				Vec2 ll = new Vec2(lVectorX, lVectorY);
				ll.normalize();

				Debug.debugManager().drawers().drawCircleImmediate(pCore.gameCamera(), lNodeA.worldPositionX + ll.x * 20.f, lNodeA.worldPositionY + ll.y * 20.f, 4.f);
			}

		}

		Debug.debugManager().drawers().endLineRenderer();

	}

}
