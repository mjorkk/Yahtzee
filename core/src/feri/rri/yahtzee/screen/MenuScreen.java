package feri.rri.yahtzee.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import feri.rri.yahtzee.Yahtzee;
import feri.rri.yahtzee.assets.AssetDescriptors;
import feri.rri.yahtzee.assets.RegionNames;
import feri.rri.yahtzee.common.GameManager;
import feri.rri.yahtzee.config.GameConfig;


public class MenuScreen extends ScreenAdapter {

    private final Yahtzee game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;
    private ImageButton quitButton;
    private ImageButton settingsButton;

    public MenuScreen(Yahtzee game) {
        this.game = game;
        assetManager = game.getAssetManager();
        assetManager.load(AssetDescriptors.MENU_MUSIC);
        assetManager.finishLoading();
        GameManager.INSTANCE.setBackgroundMusic(assetManager.get(AssetDescriptors.MENU_MUSIC));
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        TextureRegion quitButtonRegion = gameplayAtlas.findRegion(RegionNames.DICE_X);
        Drawable quitButtonDrawable = new TextureRegionDrawable(quitButtonRegion);
        quitButton = new ImageButton(quitButtonDrawable);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        quitButton.setSize(60, 60);
        quitButton.setPosition(viewport.getWorldWidth() - quitButton.getWidth() - 10f, viewport.getWorldHeight() - quitButton.getHeight() - 10f);

        TextureRegion settingsButtonRegion = gameplayAtlas.findRegion(RegionNames.DICE_SETT);
        Drawable settingsButtonDrawable = new TextureRegionDrawable(settingsButtonRegion);
        settingsButton = new ImageButton(settingsButtonDrawable); // change this line
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        settingsButton.setSize(55, 55);
        settingsButton.setPosition(0 + 10f, 10f);

        stage.addActor(createUi());
        stage.addActor(quitButton);
        stage.addActor(settingsButton);
        Gdx.input.setInputProcessor(stage);
        if (GameManager.INSTANCE.getMusicPref()) {
            GameManager.INSTANCE.getBackgroundMusic().play();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
//        ScreenUtils.clear(0f, 0f, 0f, 0f);
        if (GameManager.INSTANCE.getMusicPref()) {
            GameManager.INSTANCE.getBackgroundMusic().setLooping(true);
            GameManager.INSTANCE.getBackgroundMusic().setVolume(0.2f);
            GameManager.INSTANCE.getBackgroundMusic().play();
        }
        else {
            GameManager.INSTANCE.getBackgroundMusic().stop();
        }
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.INSTANCE.getBackgroundMusic().stop();
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton leaderboardButton = new TextButton("Scores", skin);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });



        Table buttonTable = new Table();
        buttonTable.defaults().padLeft(30).padRight(30).padBottom(10);
        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(RegionNames.MENU_BACKGROUND);
        buttonTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));

        buttonTable.add(playButton).fill();
        buttonTable.add(leaderboardButton).fillX();
//        buttonTable.add(settingsButton).padBottom(15).fillX();

        buttonTable.center();
        table.add(buttonTable).padTop(220).padBottom(130);
        table.center();
        table.setFillParent(true);
        table.pack();
        return table;
    }
}
