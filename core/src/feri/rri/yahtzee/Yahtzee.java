package feri.rri.yahtzee;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import feri.rri.yahtzee.screen.IntroScreen;
public class Yahtzee extends Game {
	private AssetManager assetManager;
	private SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		setScreen(new IntroScreen(this));
	}

	
	@Override
	public void dispose () {
		batch.dispose();
		assetManager.dispose();
	}
	public AssetManager getAssetManager() {
		return assetManager;
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
