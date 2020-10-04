package org.lintfordpickle.ld47.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lintfordpickle.ld47.data.track.Edge;
import org.lintfordpickle.ld47.data.track.Node;
import org.lintfordpickle.ld47.data.track.Track;
import org.lintfordpickle.ld47.services.TrackLoader;
import org.lwjgl.glfw.GLFW;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.Vector2f;

public class TrackEditorController extends BaseController implements IProcessMouseInput {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------
	private static List<Edge> mTempEdgeList = new ArrayList<>();
	public static final String CONTROLLER_NAME = "Track Editor Controller";

	private Track mTrack;
	public Node mSelectedNodeA;
	public int edgeLocalIndex = -1;
	public int edgeLocalIndexConstrain = -1;

	public Node mSelectedNodeB;

	private boolean mIsInMovementMode = false;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	private float mLeftMouseClickCooldown;

	public boolean isCoolDownElapsed() {
		return mLeftMouseClickCooldown < 0.f;
	}

	public void resetCoolDownTimer() {
		mLeftMouseClickCooldown = 250.f;
	}

	@Override
	public boolean isinitialized() {
		return mTrack != null;
	}

	public Track track() {
		return mTrack;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TrackEditorController(ControllerManager pControllerManager, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		// Scenery controller
		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT)) {
			return false;
			
		}

		// Toggle node enemy spawer
		if (mSelectedNodeA != null && pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_R)) {
			if (mTrack.enemySpawnNodes.contains((Integer) mSelectedNodeA.poolUid)) {
				mTrack.enemySpawnNodes.remove((Integer) mSelectedNodeA.poolUid);

			} else {
				mTrack.enemySpawnNodes.add((Integer) mSelectedNodeA.poolUid);

			}
		}

		// Toggle signal
		if (mSelectedNodeA != null && edgeLocalIndex != -1) {
			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_U)) {
				final var lEdge = mSelectedNodeA.getEdgeByIndex(edgeLocalIndex);

				if (mSelectedNodeA.numberConnectedEdges() == 3) {
					final int lEdgeUid0 = mSelectedNodeA.getOtherEdgeConnectionUids(lEdge.uid);
					final int lEdgeUid1 = mSelectedNodeA.getOtherEdgeConnectionUids2(lEdge.uid);
					lEdge.signalNode.init(mSelectedNodeA.poolUid, lEdgeUid0, lEdgeUid1);

				} else {
					lEdge.signalNode.reset();
				}

			}
		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_DELETE) && pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			mTrack.nodes.clear();
			mTrack.edges.clear();
			
			mTempEdgeList.clear();

			mSelectedNodeA = null;
			mSelectedNodeB = null;
			
			mTrack.enemySpawnNodes.clear();

			mTrack.reset();

			return true;
		}

		boolean isLeftMouseDown = pCore.input().mouse().isMouseLeftButtonDownTimed(this);

		final float lMouseWorldSpaceX = pCore.gameCamera().getMouseWorldSpaceX();
		final float lMouseWorldSpaceY = pCore.gameCamera().getMouseWorldSpaceY();

		if (mIsInMovementMode && isLeftMouseDown) {
			if (mSelectedNodeA != null) {
				mSelectedNodeA.worldPositionX = lMouseWorldSpaceX;
				mSelectedNodeA.worldPositionY = lMouseWorldSpaceY;

			}

			mIsInMovementMode = false;
			return true;
		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D) && pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			if (mSelectedNodeA != null && mSelectedNodeB != null) {
				// remove edges from between these nodes
				final var lCommonEdge = getCommonEdge(mSelectedNodeA.poolUid, mSelectedNodeB.poolUid);
				if (lCommonEdge != null) {
					deleteEdge(lCommonEdge);

				} else {
					// delete both nodes
					deleteNode(mSelectedNodeA);
					deleteNode(mSelectedNodeB);

					mSelectedNodeA = null;
					mSelectedNodeB = null;

				}

			} else if (mSelectedNodeA != null) {
				deleteNode(mSelectedNodeA);
				mSelectedNodeA = null;
			} else if (mSelectedNodeB != null) {
				deleteNode(mSelectedNodeB);
				mSelectedNodeB = null;
			}

			return true;
		}

		// toggle track constraint
		if (mSelectedNodeA != null && mSelectedNodeB == null) {
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_L)) {
				edgeLocalIndex--;
				if (edgeLocalIndex < 0)
					edgeLocalIndex = mSelectedNodeA.numberConnectedEdges() - 1;

			} else if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_K)) {
				edgeLocalIndex++;
				if (edgeLocalIndex >= mSelectedNodeA.numberConnectedEdges())
					edgeLocalIndex = 0;

			}

			// Update the currently active constraint
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_H)) {
				edgeLocalIndexConstrain--;
				if (edgeLocalIndexConstrain < 0)
					edgeLocalIndexConstrain = mSelectedNodeA.numberConnectedEdges() - 1;

			} else if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_J)) {
				edgeLocalIndexConstrain++;
				if (edgeLocalIndexConstrain >= mSelectedNodeA.numberConnectedEdges())
					edgeLocalIndexConstrain = 0;

			}

			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_O)) {
				if (edgeLocalIndex != -1 && edgeLocalIndexConstrain != -1) {
					// Toggle allowed / not allowed (ref. edgeSelected)

					final var lSelectedEdge = mSelectedNodeA.getEdgeByIndex(edgeLocalIndex);
					final var lConstrainedEdge = mSelectedNodeA.getEdgeByIndex(edgeLocalIndexConstrain);

					if (lSelectedEdge.allowedEdgeConections == null) {
						lSelectedEdge.allowedEdgeConections = new ArrayList<>();
					}

					if (lSelectedEdge.allowedEdgeConections.contains(lConstrainedEdge.uid)) {
						lSelectedEdge.allowedEdgeConections.remove((Integer) lConstrainedEdge.uid);
						System.out.println("Removed constraint uid " + lConstrainedEdge.uid + " from Edge " + lSelectedEdge.uid);

					} else {
						lSelectedEdge.allowedEdgeConections.add(lConstrainedEdge.uid);
						System.out.println("Added allow uid " + lConstrainedEdge.uid + " to Edge " + lSelectedEdge.uid);

					}

				}

			}

		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && isLeftMouseDown) {

			final var lNewNode = new Node(mTrack.getNewNodeUid());
			lNewNode.worldPositionX = lMouseWorldSpaceX;
			lNewNode.worldPositionY = lMouseWorldSpaceY;

			mTrack.nodes.add(lNewNode);

			if (mSelectedNodeB != null) {
				createEdgeBetween(lNewNode.poolUid, mSelectedNodeB.poolUid);
			} else if (mSelectedNodeA != null) {
				createEdgeBetween(lNewNode.poolUid, mSelectedNodeA.poolUid);
			}

			mSelectedNodeB = lNewNode;

		}

		else if (isLeftMouseDown) {
			if (mSelectedNodeA != null && mSelectedNodeB != null) {
				mSelectedNodeA = null;
				mSelectedNodeB = null;
				return true;
			}

			// Check for collisions with nodes
			final int lNodeCount = mTrack.nodes.size();
			for (int i = 0; i < lNodeCount; i++) {
				final var lNode = mTrack.nodes.get(i);

				if (Vector2f.distance(lMouseWorldSpaceX, lMouseWorldSpaceY, lNode.worldPositionX, lNode.worldPositionY) < 4.f) {
					if (mSelectedNodeA == null) {
						mSelectedNodeA = lNode;

						if (mSelectedNodeA.numberConnectedEdges() > 0)
							edgeLocalIndex = 0;
						else
							edgeLocalIndex = -1;

						if (mSelectedNodeA.numberConnectedEdges() > 1)
							edgeLocalIndexConstrain = 1;
						else
							edgeLocalIndexConstrain = -1;

						return true;

					} else if (mSelectedNodeB == null) {
						mSelectedNodeB = lNode;
						return true;

					}

					mSelectedNodeA = lNode;
					return true;

				}

			}

			mSelectedNodeA = null;
			mSelectedNodeB = null;

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_C)) {
			if (mSelectedNodeA != null && mSelectedNodeB != null) {
				createEdgeBetween(mSelectedNodeA.poolUid, mSelectedNodeB.poolUid);

			}

			return true;
		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_M)) {
			if (mSelectedNodeA != null) {
				mIsInMovementMode = true;

			}
		}

		// Check for collision with edges

		return super.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mLeftMouseClickCooldown -= pCore.appTime().elapsedTimeMilli();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void deleteNode(Node pNode) {
		if (pNode == null) {
			return;
		}

		mTempEdgeList.clear();

		final var lEdgeCount = pNode.numberConnectedEdges();
		for (int i = 0; i < lEdgeCount; i++) {
			mTempEdgeList.add(pNode.getEdgeByIndex(i));
		}

		for (int i = 0; i < lEdgeCount; i++) {
			final var lEdge = mTempEdgeList.get(i);
			deleteEdge(lEdge);
		}

		mTrack.nodes.remove(pNode);

	}

	private void deleteEdge(Edge pEdge) {
		if (pEdge == null)
			return;
		final int lNodeCount = mTrack.nodes.size();
		for (int i = 0; i < lNodeCount; i++) {
			final var lNode = mTrack.nodes.get(i);
			if (lNode.getEdgeByUid(pEdge.uid) != null) {
				lNode.removeEdgeByUid(pEdge.uid);
			}
		}

		Edge lEdgeToDelete = null;
		final int lEdgeCount = mTrack.edges.size();
		for (int i = 0; i < lEdgeCount; i++) {
			final var lEdge = mTrack.edges.get(i);
			if (lEdge.nodeAUid == pEdge.nodeAUid || lEdge.nodeAUid == pEdge.nodeBUid) {
				if (lEdge.nodeBUid == pEdge.nodeAUid || lEdge.nodeBUid == pEdge.nodeBUid) {
					lEdgeToDelete = lEdge;
				}
			}
		}

		mTrack.edges.remove(lEdgeToDelete);
		pEdge = null;

	}

	private Edge getCommonEdge(final int pUidA, final int pUidB) {
		final var lNodeA = mTrack.getNodeByUid(pUidA);
		final var lNodeB = mTrack.getNodeByUid(pUidB);

		if (lNodeA == null || lNodeB == null)
			return null;

		final int lEdgeCountNodeA = lNodeA.numberConnectedEdges();
		for (int i = 0; i < lEdgeCountNodeA; i++) {
			final var lOtherNodeUid = lNodeA.getEdgeByIndex(i).getOtherNodeUid(pUidA);
			if (pUidB == lOtherNodeUid) {
				return lNodeA.getEdgeByIndex(i);
			}

		}

		return null;

	}

	private void createEdgeBetween(int pNodeAUid, int pNodeBUid) {
		boolean lEdgeExists = mTrack.edgeExistsBetween(pNodeAUid, pNodeBUid);
		if (lEdgeExists)
			return; // nope

		final var lNodeA = mTrack.getNodeByUid(pNodeAUid);
		final var lNodeB = mTrack.getNodeByUid(pNodeBUid);

		if (lNodeA == null || lNodeB == null)
			return; // nope

		final var lNewEdge = new Edge(mTrack.getNewEdgeUid(), pNodeAUid, pNodeBUid);
		mTrack.edges.add(lNewEdge);

		final int lNodeAEdgeCount = lNodeA.numberConnectedEdges();
		for (int i = 0; i < lNodeAEdgeCount; i++) {
			final var lOldEdge = lNodeA.getEdgeByIndex(i);
			if (lOldEdge == null)
				return;
			if (!lOldEdge.allowedEdgeConections.contains((Integer) lNewEdge.uid)) {
				lOldEdge.allowedEdgeConections.add((Integer) lNewEdge.uid);
			}

			if (!lNewEdge.allowedEdgeConections.contains((Integer) lOldEdge.uid)) {
				lNewEdge.allowedEdgeConections.add((Integer) lOldEdge.uid);
			}

		}
		final int lNodeBEdgeCount = lNodeB.numberConnectedEdges();
		for (int i = 0; i < lNodeBEdgeCount; i++) {
			final var lOldEdge = lNodeB.getEdgeByIndex(i);
			if (lOldEdge == null)
				continue;
			if (!lOldEdge.allowedEdgeConections.contains((Integer) lNewEdge.uid)) {
				lOldEdge.allowedEdgeConections.add((Integer) lNewEdge.uid);
			}

			if (!lNewEdge.allowedEdgeConections.contains((Integer) lOldEdge.uid)) {
				lNewEdge.allowedEdgeConections.add((Integer) lOldEdge.uid);
			}

		}

		lNodeA.addEdgeToNode(lNewEdge);
		lNodeB.addEdgeToNode(lNewEdge);

	}

	public void setNewScene() {
		mTrack = new Track();

	}

	public void loadDefaultScene(String fileName) {
		mTrack = TrackLoader.loadTrackFromFile(fileName);

	}

	public void saveTrack(String pFilename) {

		FileWriter lWriter = null;
		Gson gson = new Gson();
		try {
			lWriter = new FileWriter(pFilename);
			gson.toJson(mTrack, lWriter);

		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				lWriter.flush();
				lWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
