package feri.rri.yahtzee.screen;

import static feri.rri.yahtzee.assets.RegionNames.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import feri.rri.yahtzee.CellState;
import feri.rri.yahtzee.Yahtzee;
import feri.rri.yahtzee.assets.AssetDescriptors;
import feri.rri.yahtzee.assets.RegionNames;
import feri.rri.yahtzee.common.GameManager;
import feri.rri.yahtzee.config.GameConfig;


public class GameScreen extends ScreenAdapter {

    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final Yahtzee game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    private Image infoImage;

    private Table diceTable;
    private Music backgroundMusic;

    public GameScreen(Yahtzee game) {
        this.game = game;
        assetManager = game.getAssetManager();
        assetManager.load(AssetDescriptors.GAME_MUSIC);
        assetManager.finishLoading();
        backgroundMusic = assetManager.get(AssetDescriptors.MENU_MUSIC);
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        gameplayStage.addActor(createTable());

        hudStage.addActor(createRollButton());
        hudStage.addActor(createBackButton());


        Gdx.input.setInputProcessor(new InputMultiplexer(gameplayStage, hudStage));
        if (GameManager.INSTANCE.getMusicPref()) {
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.2f);
            backgroundMusic.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(195 / 255f, 195 / 255f, 195 / 255f, 0f);

        // update
        gameplayStage.act(delta);
        hudStage.act(delta);

        // draw
        gameplayStage.draw();
        hudStage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
    }

    public void shuffleAnimation(final Image dice) {
        final Drawable[] diceFaces = new Drawable[]{new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_1)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_2)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_3)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_4)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_5)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_6))};

        final int randomNumber = new Random().nextInt(6);

        // Create a sequence action
        SequenceAction sequence = Actions.sequence();

        for (int i = 0; i < 18; i++) {
            sequence.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    dice.setDrawable(diceFaces[new Random().nextInt(6)]);
                }
            }));
            sequence.addAction(Actions.delay(0.15f));
        }
        sequence.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                dice.setDrawable(diceFaces[randomNumber]);
            }
        }));

        dice.addAction(sequence);
    }


    private Actor createTable() {
        final Table table = new Table();
        table.setDebug(false);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.PLAIN_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        diceTable = new Table();
        diceTable.defaults().padLeft(2f).padRight(2f);
        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(TABLE_BACKGROUND);
        diceTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));

        // Create the dice images and add them to the diceTable
        for (int i = 0; i < 5; i++) {
            Image dice = new Image(gameplayAtlas.findRegion(SHUFFLE_1)); // replace SHUFFLE_1 with the initial image for the dice
            diceTable.add(dice).width(10f).height(10f); // replace 100f with the desired width and height of the dice
        }

        table.add(diceTable).height(20f);
        table.row();

        table.setFillParent(true);
        table.pack();


        return table;
    }

    private Actor createRollButton() {
        TextButton rollDiceButton = new TextButton("Roll Dice", skin);
        rollDiceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (Actor actor : diceTable.getChildren()) {
                    if (actor instanceof Image) {
                        shuffleAnimation((Image) actor);
                    }
                }
            }
        });
        rollDiceButton.setWidth(170f);
        rollDiceButton.setHeight(60f);
        rollDiceButton.setPosition(diceTable.getWidth() / 2f - rollDiceButton.getWidth(), 20f);
        return rollDiceButton;
    }


    private Actor createBackButton() {
        final TextButton backButton = new TextButton("Quit", skin);
        backButton.setWidth(150f);
        backButton.setHeight(60f);
        backButton.setPosition(20f, 20f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        return backButton;
    }

    private Actor createInfo() {
        final Table table = new Table();
        table.add(new Label("Turn: ", skin));
        table.add(infoImage).size(30).row();
        table.center();
        table.pack();
        table.setPosition(GameConfig.HUD_WIDTH / 2f - table.getWidth() / 2f, GameConfig.HUD_HEIGHT - table.getHeight() - 20f);
        return table;
    }
}
