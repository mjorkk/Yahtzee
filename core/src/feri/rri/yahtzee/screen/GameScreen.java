package feri.rri.yahtzee.screen;

import static feri.rri.yahtzee.assets.RegionNames.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private Table scoreCardLabels;
    private Table scoreCardValues;
    private Music backgroundMusic;
    private boolean[] isLocked = new boolean[5];
    private final Image[] dice = new Image[5];
    private final Label[] labels = new Label[6];
    private final Map<String, Label> scoreLabels = new HashMap<>();
    private ImageButton quitButton;
    String[] categories = {
            "Ones",
            "Twos",
            "Threes",
            "Fours",
            "Fives",
            "Sixes"
    };


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
//        gameplayStage.addActor(createScoreCard());
        hudStage.addActor(createLabels());
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
        gameplayStage.act(delta);
        hudStage.act(delta);
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

    public void shuffleAnimation(final Image dice, final int index) {
        if (!isLocked[index]) {
            final Drawable[] diceFaces = new Drawable[]{new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_1)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_2)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_3)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_4)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_5)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_6))};

            final int randomNumber = new Random().nextInt(6);
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

        for (int i = 0; i < 5; i++) {
            dice[i] = new Image(gameplayAtlas.findRegion(SHUFFLE_1));
            final int index = i;
            dice[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    isLocked[index] = !isLocked[index];
                }
            });
            diceTable.add(dice[i]).width(10f).height(10f);
        }

        table.top().padTop(10f);
        table.add(diceTable).height(20f);
        table.row();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private Actor createScoreCard() {
        final Table table = new Table();
        table.setDebug(false);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.PLAIN_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        diceTable = new Table();
        diceTable.defaults().padLeft(2f).padRight(2f);
        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(TABLE_BACKGROUND);
        diceTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));

        for (String category : categories) {
            Label scoreLabel = new Label(category, skin);
            scoreLabels.put(category, scoreLabel);
            diceTable.addActor(scoreLabel);
        }

        diceTable.bottom();
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
                for (int i = 0; i < dice.length; i++) {
                    shuffleAnimation(dice[i], i);
                }
            }
        });
        rollDiceButton.setWidth(170f);
        rollDiceButton.setHeight(60f);
        rollDiceButton.setPosition(diceTable.getWidth() / 2f - rollDiceButton.getWidth(), diceTable.getY()-50f);
        return rollDiceButton;
    }
    private Actor createLabels(){
        Table table = new Table();
        table.setWidth(70f);
        for (int i=0;i<categories.length;i++) {
            labels[i]= new Label(categories[i],skin);
            table.add(labels[i]).expandX().center();
            table.row();
        }
        table.setPosition(diceTable.getWidth() / 2f,diceTable.getHeight() / 2f);
        return table;
    }
    public void updateScore(String category, int score) {
        Label scoreLabel = scoreLabels.get(category);
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(score));
        }
    }

    private Actor createBackButton() {
        TextureRegion quitButtonRegion = gameplayAtlas.findRegion(RegionNames.DICE_X);
        Drawable quitButtonDrawable = new TextureRegionDrawable(quitButtonRegion);
        quitButton = new ImageButton(quitButtonDrawable);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        quitButton.setSize(60, 60);
        quitButton.setPosition(hudViewport.getWorldWidth() - quitButton.getWidth() - 10f, hudViewport.getWorldHeight() - quitButton.getHeight() - 10f);

        return quitButton;
    }
}
