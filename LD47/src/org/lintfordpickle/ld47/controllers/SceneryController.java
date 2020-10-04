package org.lintfordpickle.ld47.controllers;

import java.io.FileWriter;
import java.io.IOException;

import org.lintfordpickle.ld47.data.SceneryProp;
import org.lintfordpickle.ld47.data.WorldScenery;
import org.lintfordpickle.ld47.services.SceneryLoader;
import org.lwjgl.glfw.GLFW;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.input.IProcessMouseInput;

public class SceneryController extends BaseController implements IProcessMouseInput {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Scenery Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ResourceController mResourceController;
	private SpriteSheetDefinition mWorldSpriteSheet;
	private WorldScenery mWorldScenery;

	public boolean isInEditMode;
	public int selectedItemIndex = 0;

	public static int getMaxSpriteIndex() {
		return 5;
	}

	public static String getSelectedSpriteName(int pIndex) {
		switch (pIndex) {
		default:
		case 0:
			return "TEXTURETREE00";
		case 1:
			return "TEXTURETREE01";
		case 2:
			return "TEXTURETREE02";
		case 3:
			return "TEXTUREDEPOTNORTH";
		case 4:
			return "TEXTUREDEPOTWEST";
		case 5:
			return "TEXTUREDEPOTSOUTH";
		}
	}

	private float mMouseLeftCoolDown;

	public boolean isCoolDownElapsed() {
		return mMouseLeftCoolDown <= 0.f;
	}

	public void resetCoolDownTimer() {
		mMouseLeftCoolDown = 200.f; // ms
	}

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isinitialized() {
		return mWorldScenery != null;
	}

	public WorldScenery worldScenery() {
		return mWorldScenery;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneryController(ControllerManager pControllerManager, WorldScenery pWorldScenery, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mWorldScenery = pWorldScenery;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

		mResourceController = (ResourceController) lControllerManager.getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	final Rectangle lTempRect = new Rectangle();

	@Override
	public boolean handleInput(LintfordCore pCore) {

		// NO TIME - PRESS AND HOLD ALT TO ENABLE OBJECT PLACEMENT
		isInEditMode = pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);

		if (isInEditMode) {
			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_F8)) {
				mWorldScenery.mSceneryItems.clear();
				return true;
			}
			
			
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DELETE)) {
				final int lNumProps = mWorldScenery.mSceneryItems.size();
				final float lMouseWorldPositionX = pCore.gameCamera().getMouseWorldSpaceX();
				final float lMouseWorldPositionY = pCore.gameCamera().getMouseWorldSpaceY();

				for (int i = 0; i < lNumProps; i++) {
					final var lPropInst = mWorldScenery.mSceneryItems.get(i);
					if (lPropInst == null)
						continue;
					lTempRect.set(lPropInst.worldPositionX, lPropInst.worldPositionY, lPropInst.objectWidth, lPropInst.objectHeight);
					if (lTempRect.intersectsAA(lMouseWorldPositionX, lMouseWorldPositionY)) {
						mWorldScenery.mSceneryItems.remove(lPropInst);
						return true;
					}
				}

				return true;
			}

			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_8)) {// toggle left
				selectedItemIndex--;
				if (selectedItemIndex < 0)
					selectedItemIndex = getMaxSpriteIndex();
				System.out.println("Selected item is : " + getSelectedSpriteName(selectedItemIndex));
			}
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_9)) { // toggle right
				selectedItemIndex++;
				if (selectedItemIndex > getMaxSpriteIndex())
					selectedItemIndex = 0;
				System.out.println("Selected item is : " + getSelectedSpriteName(selectedItemIndex));
			}

			if (pCore.input().mouse().isMouseLeftButtonDownTimed(this)) {
				final float lMouseWorldPositionX = pCore.gameCamera().getMouseWorldSpaceX();
				final float lMouseWorldPositionY = pCore.gameCamera().getMouseWorldSpaceY();

				SceneryProp lNewProp = new SceneryProp();
				lNewProp.worldPositionX = lMouseWorldPositionX;
				lNewProp.worldPositionY = lMouseWorldPositionY;
				lNewProp.spriteItemName = getSelectedSpriteName(selectedItemIndex);

				final var lSpriteFrame = mWorldSpriteSheet.getSpriteFrame(lNewProp.spriteItemName);
				lNewProp.objectWidth = lSpriteFrame.width();
				lNewProp.objectHeight = lSpriteFrame.height();

				mWorldScenery.mSceneryItems.add(lNewProp);
				System.out.println("Added prop " + lNewProp.spriteItemName + " to world " + lNewProp.worldPositionX + ", " + lNewProp.worldPositionY);

			}

		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mMouseLeftCoolDown -= pCore.appTime().elapsedTimeMilli();

		// Late load (spritesheet from controller)
		if (isInEditMode && mWorldSpriteSheet == null) {
			mWorldSpriteSheet = mResourceController.resourceManager().spriteSheetManager().getSpriteSheet("SPRITESHEET_WORLD", entityGroupID());

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void loadDefaultScene(String fileName) {
		mWorldScenery = SceneryLoader.loadSceneryFromFile(fileName);

	}

	public void saveSceneryScene(String pFilename) {

		FileWriter lWriter = null;
		Gson gson = new Gson();
		try {
			lWriter = new FileWriter(pFilename);
			gson.toJson(mWorldScenery, lWriter);

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
