package feri.rri.yahtzee.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import feri.rri.yahtzee.Yahtzee;
import feri.rri.yahtzee.assets.AssetDescriptors;
import feri.rri.yahtzee.assets.RegionNames;
import feri.rri.yahtzee.config.GameConfig;


public class IntroScreen extends ScreenAdapter {

    public static final float INTRO_DURATION_IN_SEC = 3f;   // duration of the (intro) animation

    private final Yahtzee game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private TextureAtlas gameplayAtlas;

    private float duration = 0f;

    private Stage stage;
    private Image dice;
    private int[] diceNumbers = {1, 2, 3, 4, 5, 6};
    private String[] diceN = {RegionNames.DICE_1, RegionNames.DICE_2, RegionNames.DICE_3, RegionNames.DICE_4, RegionNames.DICE_5, RegionNames.DICE_6};
    private int currentNumberIndex = 0;
    private Image background;

    public IntroScreen(Yahtzee game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());
        assetManager.load(AssetDescriptors.UI_FONT);
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.finishLoading();
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        background = new Image(gameplayAtlas.findRegion(RegionNames.BACKGROUND));
        background.setFillParent(true);
        dice = new Image(gameplayAtlas.findRegion(RegionNames.DICE_1)); // Initialize dice here
        dice.setOrigin(Align.center);
        stage.addActor(background);
        dice.setPosition(viewport.getWorldWidth() / 2f - dice.getWidth() / 2f, viewport.getWorldHeight() / 2f - dice.getHeight() / 2f);
        rollDice();
        stage.addActor(dice);
//        stage.addActor(rollDice());
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        duration += delta;

//         go to the MenuScreen after INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
//            game.setScreen(new MenuScreen(game));
        }

        stage.act(delta);
        stage.draw();
        if (dice.getActions().size == 0) {
            rollDice();
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void rollDice() {
        dice.clearActions();
        dice.setScale(1.5f);
        dice.addAction(Actions.sequence(
                Actions.rotateBy(-90, 0.5f), // Rotate by another 90 degrees
                Actions.run(new Runnable() {

                    @Override
                    public void run() {
                        currentNumberIndex = (currentNumberIndex + 1) % diceNumbers.length;
                        dice.setDrawable(new TextureRegionDrawable(gameplayAtlas.findRegion(diceN[currentNumberIndex])));


                        // Create a new line each time the dice face changes
                        final Image line = new Image(gameplayAtlas.findRegion(RegionNames.LINE));
                        line.setPosition(viewport.getWorldWidth(), viewport.getWorldHeight()/2 - dice.getHeight());
                        stage.addActor(line);

                        // Create an action that moves the line from right to left
                        Action moveLine = new Action() {
                            float speed = 1500; // Adjust this value to change the speed of the line

                            @Override
                            public boolean act(float delta) {
                                float x = line.getX() - speed * delta;

                                if (x + line.getWidth() < 0) {
                                    line.remove(); // Remove the line from the stage when it's no longer visible
                                    return true;
                                }

                                line.setX(x);
                                line.setY(dice.getY() / 2f - dice.getHeight());

                                // Calculate the distance from the line's center to the dice's center
                                float distance = Math.abs(line.getX() + line.getWidth() / 2 - dice.getX() - dice.getWidth() / 2);

                                // Calculate the factor for interpolating the line's width
                                float factor = Math.max(0, Math.min(1, distance / dice.getWidth()));

                                // Interpolate the line's width based on the factor
                                line.setWidth(Math.max(factor * line.getWidth(), dice.getWidth() / 2)); // Add a minimum width

                                return false;
                            }


                        };
                        line.addAction(moveLine);
                    }
                }),
                Actions.delay(0.1f), // Add a delay here

                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        rollDice();
                    }
                })
        ));
    }

}

