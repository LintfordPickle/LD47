package org.lintfordpickle.ld47.data.track;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseInstanceData;

public class Track extends BaseInstanceData {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private static final long serialVersionUID = -2259371698144835441L;

	private int nodeUidCounter = 0;
	private int edgeUidCounter = 0;

	public List<Node> nodes;
	public List<Edge> edges;

	public List<Integer> enemySpawnNodes;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getNewNodeUid() {
		return nodeUidCounter++;
	}

	public int getNewEdgeUid() {
		return edgeUidCounter++;
	}

	public Edge getEdgeByUid(final int pUid) {
		final int edgeCount = edges.size();
		for (int i = 0; i < edgeCount; i++) {
			if (edges.get(i).uid == pUid)
				return edges.get(i);

		}

		return null;

	}

	public Node getNodeByUid(final int pUid) {
		final int nodeCount = nodes.size();
		for (int i = 0; i < nodeCount; i++) {
			if (nodes.get(i).poolUid == pUid)
				return nodes.get(i);

		}

		return null;

	}

	public boolean edgeExistsBetween(int pUidA, int pUidB) {
		if (pUidA == pUidB)
			return false;

		final int edgeCount = edges.size();
		for (int i = 0; i < edgeCount; i++) {
			final int lEdgeNodeA = edges.get(i).nodeAUid;
			final int lEdgeNodeB = edges.get(i).nodeBUid;

			if ((lEdgeNodeA == pUidA || lEdgeNodeA == pUidB)) {
				if ((lEdgeNodeB == pUidA || lEdgeNodeB == pUidB)) {
					return true;
				}
			}

		}

		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Track() {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		enemySpawnNodes = new ArrayList<>();

	}

	public void reset() {
		nodeUidCounter = 0;
		edgeUidCounter = 0;

	}

}
