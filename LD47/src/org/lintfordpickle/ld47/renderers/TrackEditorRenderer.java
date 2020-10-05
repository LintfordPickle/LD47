package org.lintfordpickle.ld47.renderers;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.lintfordpickle.ld47.controllers.TrackEditorController;
import org.lintfordpickle.ld47.data.track.Edge;
import org.lintfordpickle.ld47.data.track.Node;
import org.lintfordpickle.ld47.data.track.Track;
import org.lintfordpickle.ld47.graphics.TrackBatchPT;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class TrackEditorRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Track Editor Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Texture mTrackTexture;
	private SpriteSheetDefinition mWorldSpriteSheet; // signals
	private TrackBatchPT mTrackBatchPCT;
	private TrackEditorController mTrackEditorController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrackEditorRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mTrackBatchPCT = new TrackBatchPT();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mTrackEditorController = (TrackEditorController) pCore.controllerManager().getControllerByNameRequired(TrackEditorController.CONTROLLER_NAME, entityGroupID());

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
	public void draw(LintfordCore pCore) {
		if (!mTrackEditorController.isinitialized())
			return;

		// Draw world origin
		Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), 0.f, 0.f);

		final var lTrack = mTrackEditorController.track();
		drawTrack(pCore, lTrack);

		drawEdges(pCore);
		drawNodes(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void drawNodes(LintfordCore pCore) {
		final var lTrack = mTrackEditorController.track();
		final var lNodeList = lTrack.nodes;

		final var lSelectedNodeA = mTrackEditorController.mSelectedNodeA;
		final var lSelectedNodeB = mTrackEditorController.mSelectedNodeB;

		final var lHudRect = pCore.HUD().boundingRectangle();
		final var lFontUnit = mRendererManager.textFont();

		lFontUnit.drawShadow(true);
		if (lSelectedNodeA != null) {
			lFontUnit.begin(pCore.HUD());
			lFontUnit.draw("Selected Node Uid A : " + lSelectedNodeA.poolUid, lHudRect.left() + 5, lHudRect.top() + 5, -0.1f, 0.f, 0.f, 1.f, 1.f, 1.f, -1);
			lFontUnit.end();

			// Show signals

			// Show constraints

		}

		if (lSelectedNodeB != null) {
			lFontUnit.begin(pCore.HUD());
			lFontUnit.draw("Selected Node Uid B : " + lSelectedNodeB.poolUid, lHudRect.left() + 5, lHudRect.top() + 25, -0.1f, 0.f, 1.f, 0.f, 1.f, 1.f, -1);
			lFontUnit.end();
		}

		lFontUnit.begin(pCore.HUD());
		lFontUnit.draw("Select two nodes and press C to create an edge", lHudRect.left() + 5, lHudRect.top() + 45, -0.1f, 1.f, 1.f, 1.f, 1.f, 1.f, -1);
		lFontUnit.end();

		GL11.glPointSize(4.f);

		final var lNodeCount = lNodeList.size();
		for (int i = 0; i < lNodeCount; i++) {
			final var lNode = lNodeList.get(i);

			if (lNode == lSelectedNodeA) {
				Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lNode.worldPositionX, lNode.worldPositionY, -0.01f, 0.f, 0.f, 1.f, 1.f);
			} else if (lNode == lSelectedNodeB) {
				Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lNode.worldPositionX, lNode.worldPositionY, -0.01f, 0.f, 1.f, 0.f, 1.f);
			} else {
				Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lNode.worldPositionX, lNode.worldPositionY, -0.01f, 1.f, 0.f, 0.f, 1.f);
			}

			if (lTrack.enemySpawnNodes.contains(lNode.poolUid)) {
				Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lNode.worldPositionX, lNode.worldPositionY, -0.01f, 1.f, 1.f, 1.f, 1.f);

			}

		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && lSelectedNodeB != null) {
			final float lWorldMouseX = pCore.gameCamera().getMouseWorldSpaceX();
			final float lWorldMouseY = pCore.gameCamera().getMouseWorldSpaceY();

			Debug.debugManager().drawers().drawLineImmediate(pCore.gameCamera(), lSelectedNodeB.worldPositionX, lSelectedNodeB.worldPositionY, lWorldMouseX, lWorldMouseY);

		} else if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && lSelectedNodeA != null) {
			final float lWorldMouseX = pCore.gameCamera().getMouseWorldSpaceX();
			final float lWorldMouseY = pCore.gameCamera().getMouseWorldSpaceY();

			Debug.debugManager().drawers().drawLineImmediate(pCore.gameCamera(), lSelectedNodeA.worldPositionX, lSelectedNodeA.worldPositionY, lWorldMouseX, lWorldMouseY);

		}

	}

	public void drawEdges(LintfordCore pCore) {
		final var lHudBounds = pCore.HUD().boundingRectangle();

		final var lTrack = mTrackEditorController.track();
		final var lEdgeList = lTrack.edges;

		final var lFontUnit = mRendererManager.textFont();

		final var lSelectedNodeA = mTrackEditorController.mSelectedNodeA;
		final var lEdgeIndex = lSelectedNodeA != null ? mTrackEditorController.edgeLocalIndex : -1;
		final var lEdgeIndexConstraint = lSelectedNodeA != null ? mTrackEditorController.edgeLocalIndexConstrain : -1;

		Edge lHighlightEdge = null;
		Edge lConstrainEdge = null;
		if (lSelectedNodeA != null && lEdgeIndex != -1) {
			lHighlightEdge = lSelectedNodeA.getEdgeByIndex(lEdgeIndex);
		}
		if (lSelectedNodeA != null && lEdgeIndexConstraint != -1) {
			lConstrainEdge = lSelectedNodeA.getEdgeByIndex(lEdgeIndexConstraint);

		}

		Debug.debugManager().drawers().beginLineRenderer(pCore.gameCamera(), GL11.GL_LINES, 2.f);

		final var lEdgeCount = lEdgeList.size();
		for (int i = 0; i < lEdgeCount; i++) {
			final var lEdge = lEdgeList.get(i);

			float lR = 1.f;
			float lG = 1.f;
			float lB = 1.f;

			if (lHighlightEdge != null && lHighlightEdge.uid == lEdge.uid) {
				lG = 0.f;

			}
			boolean lShowConstraint = false;
			boolean lIsEdgeAllowed = false;
			if (lHighlightEdge != null && lConstrainEdge != null && lConstrainEdge.uid == lEdge.uid) {
				lB = 0.f;

				if (lHighlightEdge.allowedEdgeConections == null)
					lHighlightEdge.allowedEdgeConections = new ArrayList<>();

				lIsEdgeAllowed = lHighlightEdge.allowedEdgeConections.contains((Integer) lConstrainEdge.uid);
				lShowConstraint = true;
			}

			final var lNodeA = lTrack.getNodeByUid(lEdge.nodeAUid);
			final var lNodeB = lTrack.getNodeByUid(lEdge.nodeBUid);

			Debug.debugManager().drawers().drawLine(lNodeA.worldPositionX, lNodeA.worldPositionY, lNodeB.worldPositionX, lNodeB.worldPositionY, lR, lG, lB);

			if (lShowConstraint) {
				lFontUnit.begin(pCore.HUD());
				lFontUnit.draw(lIsEdgeAllowed ? "yes" : "no", 0, 0, -0.01f, 1.f, 1.f, 1.f, 1.f, 1.f, -1);
				lFontUnit.end();

			}

			if (lEdge.signalNode != null && lEdge.signalNode.isSignalActive) {

				{
					final var lLeftEdgeUid = lEdge.signalNode.leftEdgeUid;
					final var lLeftEdge = lTrack.getEdgeByUid(lLeftEdgeUid);

					final int pCommonNodeUid = Edge.getCommonNodeUid(lEdge, lLeftEdge);

					final var lActiveNode = lTrack.getNodeByUid(pCommonNodeUid);
					final var lOtherNodeUid = lLeftEdge.getOtherNodeUid(lActiveNode.poolUid);
					final var lOtherNode = lTrack.getNodeByUid(lOtherNodeUid);
					final float lVectorX = lOtherNode.worldPositionX - lActiveNode.worldPositionX;
					final float lVectorY = lOtherNode.worldPositionY - lActiveNode.worldPositionY;

					Vec2 ll = new Vec2(lVectorX, lVectorY);
					ll.normalize();

					Debug.debugManager().drawers().drawCircleImmediate(pCore.gameCamera(), lActiveNode.worldPositionX + ll.x * 20.f, lActiveNode.worldPositionY + ll.y * 20.f, 5.f);
				}

				{
					final var lRightEdgeUid = lEdge.signalNode.rightEdgeUid;
					final var lRightEdge = lTrack.getEdgeByUid(lRightEdgeUid);

					final int pCommonNodeUid = Edge.getCommonNodeUid(lEdge, lRightEdge);

					final var lActiveNode = lTrack.getNodeByUid(pCommonNodeUid);
					final var lOtherNodeUid = lRightEdge.getOtherNodeUid(lActiveNode.poolUid);
					final var lOtherNode = lTrack.getNodeByUid(lOtherNodeUid);
					final float lVectorX = lOtherNode.worldPositionX - lActiveNode.worldPositionX;
					final float lVectorY = lOtherNode.worldPositionY - lActiveNode.worldPositionY;

					Vec2 ll = new Vec2(lVectorX, lVectorY);
					ll.normalize();

					Debug.debugManager().drawers().drawCircleImmediate(pCore.gameCamera(), lActiveNode.worldPositionX + ll.x * 20.f, lActiveNode.worldPositionY + ll.y * 20.f, 3.f);
				}

			}

		}

		if (lHighlightEdge != null) {
			lFontUnit.begin(pCore.HUD());
			lFontUnit.draw("Selected Edge Uid " + lHighlightEdge.uid, lHudBounds.left() + 5.f, lHudBounds.top() + 70.f, -0.01f, 1.f, 1.f, 1.f, 1.f, 1.f, -1);
			lFontUnit.end();
		}

		if (lConstrainEdge != null) {
			lFontUnit.begin(pCore.HUD());
			lFontUnit.draw("Constrained Edge Uid " + lConstrainEdge.uid, lHudBounds.left() + 5.f, lHudBounds.top() + 90.f, -0.01f, 1.f, 1.f, 1.f, 1.f, 1.f, -1);
			lFontUnit.end();
		}

		Debug.debugManager().drawers().endLineRenderer();

	}

	// ------------------------

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

		final var lWorldTexture = mWorldSpriteSheet.texture();

		{ // signal post

			final var lSignalBounds = lIsLeftSignalActive ? mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALLEFT") : mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALRIGHT");

			final float lLampOffsetX = pActiveEdge.signalNode.signalLampOffsetX;
			final float lLampOffsetY = pActiveEdge.signalNode.signalLampOffsetY;

			final float lWidth = lSignalBounds.width();
			final float lHeight = lSignalBounds.height();

			pTextureBatch.draw(lWorldTexture, lSignalBounds, lActiveNode.worldPositionX - lWidth * .5f + lLampOffsetX, lActiveNode.worldPositionY - lHeight + lLampOffsetY, lWidth, lHeight, -0.1f, 1, 1, 1, 1);
		}

		{ // signal box

			final var lSignalBox = mWorldSpriteSheet.getSpriteFrame("TEXTURESIGNALBOX");

			final float lBoxOffsetX = pActiveEdge.signalNode.signalBoxOffsetX;
			final float lBoxOffsetY = pActiveEdge.signalNode.signalBoxOffsetY;

			final float lWidth = lSignalBox.width();
			final float lHeight = lSignalBox.height();

			pTextureBatch.draw(lWorldTexture, lSignalBox, lActiveNode.worldPositionX - lWidth * .5f + lBoxOffsetX, lActiveNode.worldPositionY - lHeight * .5f + lBoxOffsetY, lWidth, lHeight, -0.1f, 1, 1, 1, 1);

		}

	}

}
