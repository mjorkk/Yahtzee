package feri.rri.yahtzee.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import feri.rri.yahtzee.Yahtzee;
import feri.rri.yahtzee.assets.AssetDescriptors;
import feri.rri.yahtzee.assets.RegionNames;
import feri.rri.yahtzee.common.GameManager;
import feri.rri.yahtzee.config.GameConfig;

public class SettingsScreen extends ScreenAdapter {

    private final Yahtzee game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private ButtonGroup<CheckBox> checkBoxGroup;
    private CheckBox checkBoxX;
    private CheckBox checkBoxO;
    private Skin skin;
    private TextureAtlas gameplayAtlas;

    public SettingsScreen(Yahtzee game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        stage = new Stage(viewport, game.getBatch());

        stage.addActor(createUi());
        Drawable quitButtonDrawable = new TextureRegionDrawable(gameplayAtlas.findRegion(RegionNames.LABEL_SETTINGS));
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.background = quitButtonDrawable;
        Image settingsImage = new Image(quitButtonDrawable);

        // Set the position of the image to be centered at the top of the stage
        settingsImage.setSize(300f,60f);
        settingsImage.setPosition(viewport.getWorldWidth()/ 2 - settingsImage.getWidth() / 2, viewport.getWorldHeight() - settingsImage.getHeight()-30f);

        stage.addActor(settingsImage);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

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
        Skin uiSkin = assetManager.get(AssetDescriptors.UI_SKIN);
        TextureAtlas gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.PLAIN_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        // Create checkboxes for sound and music settings
        final CheckBox soundCheckBox = new CheckBox("Sound", uiSkin);
        final CheckBox musicCheckBox = new CheckBox("Music", uiSkin);

        soundCheckBox.setChecked(GameManager.INSTANCE.getSoundPref());
        musicCheckBox.setChecked(GameManager.INSTANCE.getMusicPref());

        soundCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameManager.INSTANCE.setSoundPref(soundCheckBox.isChecked());
            }
        });
        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameManager.INSTANCE.setMusicPref(musicCheckBox.isChecked());
                if (musicCheckBox.isChecked()) {
                    GameManager.INSTANCE.getBackgroundMusic().play();
                } else {
                    GameManager.INSTANCE.getBackgroundMusic().stop();
                }
            }
        });
        TextButton backButton = new TextButton("Back", uiSkin);
        backButton.getLabel().setAlignment(Align.center);
        backButton.getLabelCell().padRight(3f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table contentTable = new Table(uiSkin);

        TextureRegion menuBackground = gameplayAtlas.findRegion(RegionNames.SETTINGS_TABLE);

        contentTable.setBackground(new TextureRegionDrawable(menuBackground));
        contentTable.add(soundCheckBox).row();
        contentTable.add(musicCheckBox).row();
        contentTable.add(backButton).width(150f).padBottom(40f).height(70f);

        table.add(contentTable).padTop(125f).padRight(100f).padLeft(100f);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
