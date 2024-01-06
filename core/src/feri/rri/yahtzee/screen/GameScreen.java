package feri.rri.yahtzee.screen;

import static feri.rri.yahtzee.assets.RegionNames.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private final Label[] labelsUpper = new Label[6];
    private final Label[] labelsLower = new Label[7];
    private Array<Integer> scoreUpper = new Array<Integer>(6);
    private Array<Integer> scoreLower = new Array<Integer>(7);

    private final TextField[] scoresUpper = new TextField[6];
    private final TextField[] scoresLower = new TextField[7];
    private ImageButton quitButton;
    private Integer rollCount = 0;
    String[] categoriesUpper = {
            "Ones",
            "Twos",
            "Threes",
            "Fours",
            "Fives",
            "Sixes"
    };
    String[] categoriesLower = {
            "Three of a kind",
            "Four of a kind",
            "Full House",
            "Small straight",
            "Large straight",
            "Chance",
            "YAHTZEE"
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
        hudStage.addActor(createUpperSection());
        hudStage.addActor(createLowerSection());
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
        Gdx.gl.glClearColor(165 / 255f, 150 / 255f, 136 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
                sequence.addAction(Actions.delay(0.1f));
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
                    if (isLocked[index]) {
                        dice[index].addAction(Actions.alpha(0.5f));
                    } else {
                        dice[index].addAction(Actions.alpha(1f));
                    }
                }
            });
            diceTable.add(dice[i]).width(8f).height(8f).padLeft(3.5f).padRight(3.5f).padBottom(2f);
        }

        table.top().padTop(12f);
        table.add(diceTable).height(15f);
        table.row();
        table.setFillParent(true);
        table.pack();

        return table;
    }


    private Actor createRollButton() {
        final TextButton rollDiceButton = new TextButton("Roll Dice", skin);
        rollDiceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rollDiceButton.isDisabled()) return;
                rollCount++;
                rollDiceButton.setDisabled(true);
                for (int i = 0; i < dice.length; i++) {
                    dice[i].setTouchable(Touchable.disabled);
                    shuffleAnimation(dice[i], i);
                }
                rollDiceButton.addAction(Actions.sequence(
                        Actions.delay(2.5f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                if (rollCount != 3)
                                    rollDiceButton.setDisabled(false);
                                for (Image die : dice) {
                                    die.setTouchable(Touchable.enabled);
                                }
                            }
                        })
                ));
            }
        });
        rollDiceButton.setWidth(170f);
        rollDiceButton.setHeight(60f);
        rollDiceButton.setPosition(diceTable.getWidth() / 2f - rollDiceButton.getWidth(), hudStage.getHeight() - 100f);
        return rollDiceButton;
    }

    private Actor createUpperSection() {
        Table table = new Table();
        table.setWidth(70f);
        for (int i = 0; i < categoriesUpper.length; i++) {
            labelsUpper[i] = new Label(categoriesUpper[i], skin);
            scoresUpper[i] = new TextField("x", skin);
            scoresUpper[i].setAlignment(Align.center);
            scoresUpper[i].setDisabled(true);
            table.add(labelsUpper[i]).expandX().pad(5f).left();
            table.add(scoresUpper[i]).padLeft(40f).height(40f).width(70f);
            table.row();
        }
        table.setPosition(110f, 245f);
        return table;
    }

    private Actor createLowerSection() {
        Table table = new Table();
        table.setWidth(70f);
        for (int i = 0; i < categoriesLower.length; i++) {
            labelsLower[i] = new Label(categoriesLower[i], skin);
            scoresLower[i] = new TextField("", skin);
            scoresLower[i].setAlignment(Align.center);
            scoresLower[i].setDisabled(true);
            table.add(labelsLower[i]).expandX().pad(5f).left();
            table.add(scoresLower[i]).padLeft(40f).height(40f).width(70f);
            table.row();
        }
        table.setPosition(540f, 220f);
        return table;
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
        quitButton.setPosition(hudViewport.getWorldWidth() - quitButton.getWidth() - 10f, hudViewport.getWorldHeight() - quitButton.getHeight() - 30f);

        return quitButton;
    }
}
