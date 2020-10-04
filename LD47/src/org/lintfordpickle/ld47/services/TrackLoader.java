package org.lintfordpickle.ld47.services;

import org.lintfordpickle.ld47.controllers.Box2dGameController;
import org.lintfordpickle.ld47.data.physicsdata.NodePhysicsData;
import org.lintfordpickle.ld47.data.track.Track;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class TrackLoader {

	final static float lNodeRadius = 0.25f;

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static Track loadTrackFromFile(String pFilename) {
		final var lGson = new GsonBuilder().create();
		String lTrackRawFileContents = null;
		Track lTrack = null;

		try {
			lTrackRawFileContents = FileUtils.loadString(pFilename);
			lTrack = lGson.fromJson(lTrackRawFileContents, Track.class);

		} catch (JsonSyntaxException ex) {
			Debug.debugManager().logger().printException(TrackLoader.class.getSimpleName(), ex);

		}

		if (lTrack == null) {
			lTrack = new Track();
		}

		final int lNodeCount = lTrack.nodes.size();
		for (int i = 0; i < lNodeCount; i++) {
			lTrack.nodes.get(i).resolveEdges(lTrack);

		}

		return lTrack;
	}

	public static Track createPhysicsObjects(Track pTrack, final ControllerManager pControllerManager, int pEntityGroupUid) {
		Box2dGameController lBox2dController = (Box2dGameController) pControllerManager.getControllerByNameRequired(Box2dGameController.CONTROLLER_NAME, pEntityGroupUid);

		// Build the nodes (Box2d)
		final int lNodeCount = pTrack.nodes.size();
		for (int i = 0; i < lNodeCount; i++) {
			final var lNode = pTrack.nodes.get(i);
			final var lPhysicsObject = lBox2dController.getNodePhysicsObject(lNodeRadius);
			lPhysicsObject.userDataObject(new NodePhysicsData(lNode.poolUid));
			lNode.setPhysicsObject(lPhysicsObject);

		}

		return pTrack;
	}

}
