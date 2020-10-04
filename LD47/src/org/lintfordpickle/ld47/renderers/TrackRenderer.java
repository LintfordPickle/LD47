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

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

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

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTrackBatchPCT.unloadGLContent();
		mWorldSpriteSheet = null;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		mLeftMouseCooldownTimer -= pCore.appTime().elapsedTimeMilli();

		final float lMouseWorldSpaceX = pCore.gameCamera().getMouseWorldSpaceX();
		final float lMouseWorldSpaceY = pCore.gameCamera().getMouseWorldSpaceY();

		if (pCore.input().mouse().isMouseLeftButtonDownTimed(this)) {
			final var lTrack = mTrackController.track();
			final int lNodeCount = lTrack.nodes.size();
			for (int i = 0; i < lNodeCount; i++) {
				final var lNode = lTrack.nodes.get(i);

				if (lNode != null && Vector2f.distance(lMouseWorldSpaceX, lMouseWorldSpaceY, lNode.worldPositionX, lNode.worldPositionY) < 6.f) {
					final int lEdgeCount = lNode.numberConnectedEdges();
					for (int j = 0; j < lEdgeCount; j++) {
						if(lNode.getEdgeByIndex(j) == null) continue;
						if (lNode.getEdgeByIndex(j).signalNode.isSignalActive) {
							lNode.getEdgeByIndex(j).signalNode.toggleSignal();
						}
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
		final float lPosW = 8.f;
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

		Vec2 ll = new Vec2(lVectorX, lVectorY);
		ll.normalize();

		final var lWorldTexture = mWorldSpriteSheet.texture();
		final var lSignalBounds = lIsLeftSignalActive ? mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALLEFT") : mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALRIGHT");
		pTextureBatch.draw(lWorldTexture, lSignalBounds, lActiveNode.worldPositionX - 8.f, lActiveNode.worldPositionY - 16.f, 16.f, 16.f, -0.1f, 1, 1, 1, 1);

		// Debug.debugManager().drawers().drawCircleImmediate(pCore.gameCamera(), lActiveNode.worldPositionX + ll.x * 20.f, lActiveNode.worldPositionY + ll.y * 20.f, 4.f);

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
