package org.lintfordpickle.ld47.data.track;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.JBox2dEntity;
import net.lintford.library.core.maths.RandomNumbers;

public class Node extends JBox2dEntity {

	private static final long serialVersionUID = -376552211463747406L;

	private static final List<Edge> EDGE_UPDATE_LIST = new ArrayList<>();

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	/**
	 * All edges connected to this node. Just because an edge is connecetd to a node, doesn't mean the edge can be traversed.
	 */
	private transient List<Edge> connectedEdges;
	private List<Integer> connectedEdgeUids;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int numberConnectedEdges() {
		return connectedEdges.size();
	}

	public void addEdgeToNode(Edge pEdge) {
		if (!connectedEdges.contains(pEdge)) {
			connectedEdges.add(pEdge);
			connectedEdgeUids.add(pEdge.uid);

		}

	}

	@Override
	public boolean isAssigned() {
		return true;
	}

	public Edge getEdgeByIndex(int pEdgeListIndex) {
		return connectedEdges.get(pEdgeListIndex);
	}

	public Edge getEdgeByUid(int pEdgeUid) {
		final int lEdgeCount = connectedEdges.size();
		for (int i = 0; i < lEdgeCount; i++) {
			if (connectedEdges.get(i) == null)
				continue;

			if (connectedEdges.get(i).uid == pEdgeUid) {
				return connectedEdges.get(i);
			}
		}

		return null;
	}

	public void removeEdgeByUid(int pEdgeUid) {
		EDGE_UPDATE_LIST.clear();
		final int lEdgeCount = connectedEdges.size();
		for (int i = 0; i < lEdgeCount; i++) {
			EDGE_UPDATE_LIST.add(connectedEdges.get(i));
		}

		for (int i = 0; i < lEdgeCount; i++) {
			final var lEdge = EDGE_UPDATE_LIST.get(i);
			if (lEdge != null && lEdge.uid == pEdgeUid) {
				connectedEdges.remove(lEdge);
			}
		}

		if (connectedEdgeUids.contains((Integer) pEdgeUid)) {
			connectedEdgeUids.remove((Integer) pEdgeUid);
		}

	}

	public Edge getRandomEdgeApartFrom(List<Integer> pEdgeUidWhiteList, int pComingFromUid) {
		if (connectedEdges == null || connectedEdges.size() == 0)
			return null;

		EDGE_UPDATE_LIST.clear();

		final int lEdgeCount = connectedEdges.size();
		for (int i = 0; i < lEdgeCount; i++) {
			if (connectedEdges.get(i) == null)
				continue;
			final int lUidToCheck = connectedEdges.get(i).uid;
			if (lUidToCheck != pComingFromUid && pEdgeUidWhiteList.contains(lUidToCheck)) {
				EDGE_UPDATE_LIST.add(connectedEdges.get(i));
			}

		}

		if (EDGE_UPDATE_LIST.size() == 1)
			return EDGE_UPDATE_LIST.get(0);

		final int lUEdgeCount = EDGE_UPDATE_LIST.size();

		if (lUEdgeCount == 0) {
			return null;
		}

		final int lRandIndex = RandomNumbers.random(0, lUEdgeCount);

		return EDGE_UPDATE_LIST.get(lRandIndex);

	}

	public Edge getRandomEdge() {
		if (connectedEdges == null || connectedEdges.size() == 0)
			return null;

		final int lEdgeCount = connectedEdges.size();
		final int lRandIndex = RandomNumbers.random(0, lEdgeCount);

		return connectedEdges.get(lRandIndex);

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Node(final int pUid) {
		super(pUid);

		connectedEdges = new ArrayList<>();
		connectedEdgeUids = new ArrayList<>();

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public void resolveEdges(Track pTrack) {
		if (connectedEdges == null)
			connectedEdges = new ArrayList<>();

		final int lEdgeUidCount = connectedEdgeUids.size();
		for (int i = 0; i < lEdgeUidCount; i++) {
			final var lEdge = pTrack.getEdgeByUid(connectedEdgeUids.get(i));
			if (lEdge != null && !connectedEdges.contains(lEdge)) {
				connectedEdges.add(lEdge);

			} else {
				throw new RuntimeException("Error loading track");
			}
		}

	}

	public int getOtherEdgeConnectionUids(int pNotThisEdgeUid) {
		final int allowedEdgeCount = connectedEdgeUids.size();
		for (int i = 0; i < allowedEdgeCount; i++) {
			if (connectedEdgeUids.get(i) != pNotThisEdgeUid)
				return connectedEdgeUids.get(i);
		}

		return -1;

	}

	public int getOtherEdgeConnectionUids2(int pOurEdgeUid) {
		boolean lFoundOne = false;
		final int allowedEdgeCount = connectedEdgeUids.size();
		for (int i = 0; i < allowedEdgeCount; i++) {
			if (connectedEdgeUids.get(i) != pOurEdgeUid) {
				if (!lFoundOne)
					lFoundOne = true;
				else
					return connectedEdgeUids.get(i);
			}
		}

		return -1;

	}

}
