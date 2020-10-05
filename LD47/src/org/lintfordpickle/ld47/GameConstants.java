package org.lintfordpickle.ld47;

public class GameConstants {

	// [GameStateController] Enables win and lose conditions
	// [EnemySpawnController] Game end conditions don't affect spawning if on 
	public static boolean PREVIEW_MODE = false;
	
	public static boolean ENABLE_BOX2D_DEBUG_DRAW = false;
	
	// When in debug mode, there is no constraints on zoom factor
	// [CameraZoomController]
	// When in debug mode, there is no constraints on movement bounds
	// [CameraMovementController]
	public static boolean CAMERA_DEBUG_MODE = false;
	
	// [MainMenuScreen]
	public static boolean ENABLE_TRACK_EDITOR_IN_MENU = false;
	
	// Sets whether enemy trains can spawn
	// [EnemySpawnController]
	public static boolean ENABLE_ENEMY_SPAWNING = true;
	
	// Controls the amount of time between enemy train spawns (random value)
	// [EnemySpawnController]
	public static float ENEMY_SPAWN_MIN_TIME_MS = 4000.0f;
	public static float ENEMY_SPAWN_MAX_TIME_MS = 8000.0f;
	
	// Issue with carriages means they must be disbaled for now
 	// [TrainController]
	public static final boolean FORCE_NO_CARRIAGES_BECAUSE_BROKEN = true;
	
}
