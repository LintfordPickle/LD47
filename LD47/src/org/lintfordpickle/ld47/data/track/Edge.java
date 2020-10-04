package org.lintfordpickle.ld47.data.track;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseInstanceData;

public class Edge extends BaseInstanceData {

	private static final long serialVersionUID = 8253048654834320875L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public final int uid;

	public int nodeAUid;
	public int nodeBUid;

	public class Signal {
		public boolean isSignalActive; // not all nodes are elligable
		public int leftEdgeUid = -1;
		public int rightEdgeUid = -1;
		public int signalNodeUid = -1;
		public boolean leftEnabled;

		public void reset() {
			isSignalActive = false;
			leftEdgeUid = -1;
			signalNodeUid = -1;
			rightEdgeUid = -1;
		}

		public void init(int pNodeUid, int pLeftEdgeUid, int pRightEdgeUid) {
			if (pLeftEdgeUid == -1 || pRightEdgeUid == -1) {
				reset();
				return;

			}

			isSignalActive = true;
			signalNodeUid = pNodeUid;
			leftEdgeUid = pLeftEdgeUid;
			rightEdgeUid = pRightEdgeUid;

		}

		public void toggleSignal() {
			leftEnabled = !leftEnabled;
		}

	}

	public final Signal signalNode = new Signal();

	/** A train leaving one of the edges contained in this list can travse this edge */
	public List<Integer> allowedEdgeConections = new ArrayList<>();

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public Edge(final int pUid) {
		uid = pUid;

	}

	public Edge(final int pUid, int pNodeAUid, int pNodeBUid) {
		this(pUid);

		nodeAUid = pNodeAUid;
		nodeBUid = pNodeBUid;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public int getOtherNodeUid(int pNodeUid) {
		if (nodeAUid == pNodeUid) {
			return nodeBUid;

		} else {
			return nodeAUid;

		}
	}

	public int getOtherAllowedEdgeConnectionUids(int pOurEdgeUid) {
		final int allowedEdgeCount = allowedEdgeConections.size();
		for (int i = 0; i < allowedEdgeCount; i++) {
			if (allowedEdgeConections.get(i) != pOurEdgeUid)
				return allowedEdgeConections.get(i);
		}

		return -1;

	}

	public int getOtherAllowedEdgeConnectionUids2(int pOurEdgeUid) {
		boolean lFoundOne = false;
		final int allowedEdgeCount = allowedEdgeConections.size();
		for (int i = 0; i < allowedEdgeCount; i++) {
			if (allowedEdgeConections.get(i) != pOurEdgeUid) {
				if (!lFoundOne)
					lFoundOne = true;
				else
					return allowedEdgeConections.get(i);
			}
		}

		return -1;

	}

	public static int getCommonNodeUid(Edge pEdgeA, Edge pEdgeB) {
		if (pEdgeA.nodeAUid == pEdgeB.nodeAUid)
			return pEdgeB.nodeAUid;
		else if (pEdgeA.nodeBUid == pEdgeB.nodeBUid)
			return pEdgeB.nodeBUid;
		else if (pEdgeA.nodeAUid == pEdgeB.nodeBUid)
			return pEdgeB.nodeBUid;
		else if (pEdgeA.nodeBUid == pEdgeB.nodeAUid)
			return pEdgeB.nodeAUid;
		return -1;
	}

}
