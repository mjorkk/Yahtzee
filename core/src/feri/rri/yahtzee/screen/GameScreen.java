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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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

import feri.rri.yahtzee.GameResult;
import feri.rri.yahtzee.Yahtzee;
import feri.rri.yahtzee.assets.AssetDescriptors;
import feri.rri.yahtzee.assets.RegionNames;
import feri.rri.yahtzee.common.GameManager;
import feri.rri.yahtzee.config.GameConfig;


public class GameScreen extends ScreenAdapter {

    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final Yahtzee game;
    private Drawable[] diceFaces;

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
    private Music diceRollSound;
    private boolean[] isLocked = new boolean[5];
    private final Image[] dice = new Image[5];
    private Array<Integer> diceValues = new Array<Integer>(5);
    private Array<Integer> occurences = new Array<Integer>(6);
    private Label[] alerts = new Label[2];

    private final Label[] labelsUpper = new Label[7];
    private final Label[] labelsLower = new Label[7];
    private TextButton rollDiceButton;
    private Integer rollLimit;

    private final TextField[] scoresUpper = new TextField[7];
    private final TextField[] scoresLower = new TextField[7];
    private Array<Integer> scores = new Array<Integer>(14);
    private ImageButton quitButton;
    private Integer sumOfDice = 0;
    private Integer sumOfBonus = 0;
    private Integer rollCount = 0;
    private Integer finalScore = 0;
    private Integer combLeft = 13;

    String[] categoriesUpper = {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Bonus"};
    String[] categoriesLower = {"Three of a kind", "Four of a kind", "Full House", "Small straight", "Large straight", "Chance", "YAHTZEE"};


    public GameScreen(Yahtzee game) {
        this.game = game;
        assetManager = game.getAssetManager();
        assetManager.load(AssetDescriptors.GAME_MUSIC);
        assetManager.load(AssetDescriptors.DICE_ROLL);
        assetManager.finishLoading();
        backgroundMusic = assetManager.get(AssetDescriptors.GAME_MUSIC);
        diceRollSound = assetManager.get(AssetDescriptors.DICE_ROLL);
        rollLimit = GameManager.INSTANCE.getLuckTestPref() ? 2 : 3;
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        diceFaces = new Drawable[]{new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_1)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_2)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_3)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_4)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_5)), new TextureRegionDrawable(gameplayAtlas.findRegion(SHUFFLE_6))};
        diceValues.addAll(1, 1, 1, 1, 1);
        occurences.addAll(0, 0, 0, 0, 0, 0);
        scores.addAll(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);
        gameplayStage.addActor(createTable());
        hudStage.addActor(createAlerts());
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

    private Actor createAlerts() {
        alerts[0] = new Label("Choose your move!", skin, "alt-label");
        alerts[1] = new Label("CONGRATULATIONS! Your final score is ", skin, "alt-label");
        alerts[0].setPosition(hudViewport.getWorldWidth() / 2f - alerts[0].getWidth() / 2f, hudViewport.getWorldHeight() / 3f * 2f - 70f);
        alerts[1].setPosition(hudViewport.getWorldWidth() / 2f - alerts[1].getWidth() / 2f, hudViewport.getWorldHeight() / 3f * 2f - 70f);
        alerts[0].setVisible(false);
        hudStage.addActor(alerts[1]);
        alerts[1].setVisible(false);
        return alerts[0];
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
            diceValues.set(index, randomNumber + 1);
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
                    if(rollCount!=0) {
                        isLocked[index] = !isLocked[index];
                        if (isLocked[index]) {
                            dice[index].addAction(Actions.alpha(0.5f));
                        } else {
                            dice[index].addAction(Actions.alpha(1f));
                        }
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

    private void checkForEnd() {
        if (combLeft <= 0) {
            alerts[0].setVisible(false);
            alerts[1].setText(alerts[1].getText() + String.valueOf(finalScore));
            alerts[1].setPosition(hudViewport.getWorldWidth() / 2f - alerts[1].getWidth() / 2f, hudViewport.getWorldHeight() / 3f * 2f - 70f);
            alerts[1].setVisible(true);
            rollDiceButton.setDisabled(true);
            GameResult res = new GameResult(finalScore,GameManager.INSTANCE.getPlayerName());
            GameManager.saveGameResult(res);

        }
    }


    private Actor createRollButton() {
        rollDiceButton = new TextButton("Roll Dice", skin);
        rollDiceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rollDiceButton.isDisabled()) return;
                if (GameManager.INSTANCE.getSoundPref()) {
                    diceRollSound.play();
                }
                clearScores();
                occurences.clear();
                occurences.addAll(0, 0, 0, 0, 0, 0);
                rollCount++;
                if (rollCount == rollLimit) {
                    alerts[0].setVisible(true);
                    rollDiceButton.addAction(Actions.alpha(0.5f));
                }
                rollDiceButton.setDisabled(true);
                for (int i = 0; i < dice.length; i++) {
                    dice[i].setTouchable(Touchable.disabled);
                    shuffleAnimation(dice[i], i);
                }
                rollDiceButton.addAction(Actions.sequence(Actions.delay(2.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        updateScore();
                        if (rollCount != rollLimit) rollDiceButton.setDisabled(false);
                        for (Image die : dice) {
                            die.setTouchable(Touchable.enabled);
                        }
                    }
                })));

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
            scoresUpper[i] = new TextField("", skin);
            scoresUpper[i].setAlignment(Align.center);
            scoresUpper[i].setDisabled(true);
            final int finalI = i;
            if (i != 6) scoresUpper[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final TextField textField = (TextField) event.getTarget();
                    String text = textField.getText();
                    if (text.isEmpty() && rollCount != 0) {
                        Dialog dialog = new Dialog("Confirm", skin) {
                            protected void result(Object object) {
                                if ((Boolean) object) {
                                    scoresUpper[finalI].addAction(Actions.alpha(0.5f));
                                    scores.set(finalI, 0);
                                    textField.setText("0");
                                    setScore();
                                }
                            }
                        };
                        dialog.text("Do you want to score 0 points in this category?");
                        dialog.button("Yes", true);
                        dialog.button("No", false);
                        dialog.show(hudStage);
                    } else if (rollCount != 0) {
                        if (scores.get(finalI) == -1) {
                            scoresUpper[finalI].addAction(Actions.alpha(0.5f));
                            int score = Integer.parseInt(text);
                            scores.set(finalI, score);
                            finalScore += score;
                            setScore();
                        }
                    }
                }

                public void setScore() {
                    rollCount = 0;
                    combLeft--;
                    rollDiceButton.setDisabled(false);
                    clearScores();
                    alerts[0].setVisible(false);
                    rollDiceButton.addAction(Actions.alpha(1f));
                    checkForEnd();
                }
            });
            table.add(labelsUpper[i]).expandX().pad(5f).left();
            table.add(scoresUpper[i]).padLeft(40f).height(40f).width(70f);
            table.row();
        }
        table.setPosition(110f, 210f);
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
            final int finalI = i;
            scoresLower[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final TextField textField = (TextField) event.getTarget();
                    String text = textField.getText();
                    if (text.isEmpty() && rollCount != 0) {
                        Dialog dialog = new Dialog("Confirm", skin) {
                            protected void result(Object object) {
                                if ((Boolean) object) {
                                    scoresLower[finalI].addAction(Actions.alpha(0.5f));
                                    scores.set(finalI + 7, 0);
                                    textField.setText("0");
                                    setScore();
                                }
                            }
                        };
                        dialog.text("Do you want to score 0 points in this category?");
                        dialog.button("Yes", true);
                        dialog.button("No", false);
                        dialog.show(hudStage);
                    } else if (rollCount != 0) {
                        if (scores.get(finalI + 7) == -1) {
                            scoresLower[finalI].addAction(Actions.alpha(0.5f));
                            int score = Integer.parseInt(text);
                            scores.set(finalI + 7, score);
                            finalScore += score;
                            setScore();
                        }
                    }
                }

                public void setScore() {
                    rollCount = 0;
                    combLeft--;
                    rollDiceButton.setDisabled(false);
                    clearScores();
                    alerts[0].setVisible(false);
                    rollDiceButton.addAction(Actions.alpha(1f));
                    checkForEnd();
                }
            });
            table.add(labelsLower[i]).expandX().pad(5f).left();
            table.add(scoresLower[i]).padLeft(40f).height(40f).width(70f);
            table.row();
        }
        table.setPosition(540f, 210f);
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

    public void updateScore() {
        countOccurences();
        for (int i = 0; i < occurences.size; i++) {
            int score = (i + 1) * occurences.get(i);
            if (score > 0 && scores.get(i) < 0) {
                scoresUpper[i].setText(String.valueOf(score));
            }
        }
        getSum();
        if (scores.get(12) < 0) scoresLower[5].setText(String.valueOf(sumOfDice));
        if (sumOfBonus >= 63 && scores.get(6) < 0)
            scoresUpper[6].setText(String.valueOf(sumOfBonus));

        // lower section
        for (int i = 0; i < occurences.size; i++) {
            if (occurences.get(i) >= 3 && scores.get(7) < 0) {
                scoresLower[0].setText(String.valueOf(sumOfDice));
            }
            if (occurences.get(i) >= 4 && scores.get(8) < 0) {
                scoresLower[1].setText(String.valueOf(sumOfDice));
            }
            if (occurences.get(i) == 5 && scores.get(13) < 0) {
                Gdx.app.log("debug", "Yahtzee with face " + (i + 1));
                scoresLower[6].setText("50");
            }
        }

        // full house, small straight, and large straight
        if (occurences.contains(2, false) && occurences.contains(3, false) && scores.get(9) < 0) {
            scoresLower[2].setText("25");
        }
        if (isStraight(occurences, 4) && scores.get(10) < 0) {
            scoresLower[3].setText("30");
        }
        if (isStraight(occurences, 5) && scores.get(11) < 0) {
            scoresLower[4].setText("40");
        }
    }

    private void getSum() {
        sumOfDice = 0;
        sumOfBonus = 0;
        for (int i = 0; i < 5; i++)
            sumOfDice += diceValues.get(i);
        for (int i = 0; i < 6; i++)
            sumOfBonus += scores.get(i);
    }

    private boolean isStraight(Array<Integer> occurrences, int length) {
        int consecutive = 0;
        for (int i = 0; i < occurrences.size; i++) {
            if (occurrences.get(i) > 0) {
                consecutive++;
                if (consecutive == length) {
                    return true;
                }
            } else {
                consecutive = 0;
            }
        }
        return false;
    }

    private void countOccurences() {
        for (int j = 0; j < 5; j++) {
            occurences.set(diceValues.get(j) - 1, occurences.get(diceValues.get(j) - 1) + 1);
        }
    }

    private void clearScores() {
        if (rollCount == 0)
            for (int i = 0; i < 5; i++) {
                dice[i].addAction(Actions.alpha(1f));
                isLocked[i] = false;
            }
        for (int i = 0; i < 14; i++) {
            if (scores.get(i) >= 0) continue;
            if (i >= 7) scoresLower[i % 7].setText("");
            else scoresUpper[i].setText("");
        }
    }
}
