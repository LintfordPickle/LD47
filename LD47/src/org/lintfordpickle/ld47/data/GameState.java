package org.lintfordpickle.ld47.data;

import net.lintford.library.core.entity.PooledBaseData;

public class GameState extends PooledBaseData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -4614085384213757212L;

	private final static int PLAYER_STARTING_HEALTH = 5;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int playerHealth;
	public float timeAlive;
	public float timeRemaining;
	public int numberCollisions;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isAssigned() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameState(int pPoolUid) {
		super(pPoolUid);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame(float pTargetTimeInMillis) {
		playerHealth = PLAYER_STARTING_HEALTH;
		numberCollisions = 0;
		timeAlive = 0.f;
		timeRemaining = pTargetTimeInMillis;

	}

}
