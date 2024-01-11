package feri.rri.yahtzee.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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

        settingsImage.setSize(300f, 60f);
        settingsImage.setPosition(viewport.getWorldWidth() / 2 - settingsImage.getWidth() / 2, viewport.getWorldHeight() - settingsImage.getHeight() - 30f);

        stage.addActor(settingsImage);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(165 / 255f, 150 / 255f, 136 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.DOTTED_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        final CheckBox luckTestCheckBox = new CheckBox("I'm feeling lucky!", uiSkin);

        luckTestCheckBox.setChecked(GameManager.INSTANCE.getLuckTestPref());

        luckTestCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(luckTestCheckBox.isChecked()){
                    GameManager.INSTANCE.setLuckTestPref(true);
                    luckTestCheckBox.setChecked(true);
                }else {
                    GameManager.INSTANCE.setLuckTestPref(false);
                    luckTestCheckBox.setChecked(false);
                }
            }
        });

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
        contentTable.add(skinOptions()).row();
        contentTable.add(luckTestCheckBox).padTop(20f).row();
        contentTable.add(soundCheckBox).padTop(10f).row();
        contentTable.add(musicCheckBox).padTop(10f).row();
        contentTable.add(backButton).width(150f).padBottom(40f).padTop(20f).height(70f);

        table.add(contentTable).padTop(125f).padRight(100f).padLeft(100f);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private Table skinOptions() {
        Table table = new Table();
        Skin uiSkin = assetManager.get(AssetDescriptors.UI_SKIN);
        Label instructionLabel = new Label("Choose a skin: ", uiSkin);
        table.add(instructionLabel).colspan(3).padBottom(20f).padTop(20f).row();
        final ImageButton skin1Button = new ImageButton(new TextureRegionDrawable(gameplayAtlas.findRegion("shuffle-1")));

        final ImageButton skin2Button = new ImageButton(new TextureRegionDrawable(gameplayAtlas.findRegion("2shuffle-1")));
        final ImageButton skin3Button = new ImageButton(new TextureRegionDrawable(gameplayAtlas.findRegion("3shuffle-1")));
        skin1Button.addAction(Actions.alpha(0.5f));
        skin2Button.addAction(Actions.alpha(0.5f));
        skin3Button.addAction(Actions.alpha(0.3f));
        final Label skin1Label = new Label("Old\nClassic", uiSkin, "small");
        skin1Label.setAlignment(Align.center);

        final Label skin2Label = new Label("Rubik's\nRoll", uiSkin, "small");
        skin2Label.setAlignment(Align.center);

        final Label skin3Label = new Label("Lovelee\nYahtzee", uiSkin, "small");
        skin3Label.setAlignment(Align.center);
        skin1Label.addAction(Actions.alpha(0.5f));
        skin2Label.addAction(Actions.alpha(0.5f));
        skin3Label.addAction(Actions.alpha(0.5f));

        if (GameManager.INSTANCE.getSkinPref() == 1) {
            skin1Button.addAction(Actions.alpha(1f));
            skin1Label.addAction(Actions.alpha(1f));
        } else if (GameManager.INSTANCE.getSkinPref() == 2) {
            skin2Button.addAction(Actions.alpha(1f));
            skin2Label.addAction(Actions.alpha(1f));
        } else {
            skin3Button.addAction(Actions.alpha(1f));
            skin3Label.addAction(Actions.alpha(1f));
        }
        skin1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                skin1Button.addAction(Actions.alpha(1f));
                skin2Button.addAction(Actions.alpha(0.5f));
                skin3Button.addAction(Actions.alpha(0.3f));
                skin1Label.addAction(Actions.alpha(1f));
                skin2Label.addAction(Actions.alpha(0.5f));
                skin3Label.addAction(Actions.alpha(0.5f));
                RegionNames.setSkin1();
                GameManager.INSTANCE.setSkinPref(1);
            }
        });
        skin2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                skin1Button.addAction(Actions.alpha(0.5f));
                skin2Button.addAction(Actions.alpha(1f));
                skin3Button.addAction(Actions.alpha(0.3f));
                skin1Label.addAction(Actions.alpha(0.5f));
                skin2Label.addAction(Actions.alpha(1f));
                skin3Label.addAction(Actions.alpha(0.5f));
                RegionNames.setSkin2();
                GameManager.INSTANCE.setSkinPref(2);
            }
        });
        skin3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                skin1Button.addAction(Actions.alpha(0.5f));
                skin2Button.addAction(Actions.alpha(0.5f));
                skin3Button.addAction(Actions.alpha(1f));
                skin1Label.addAction(Actions.alpha(0.5f));
                skin2Label.addAction(Actions.alpha(0.5f));
                skin3Label.addAction(Actions.alpha(1f));
                RegionNames.setSkin3();
                GameManager.INSTANCE.setSkinPref(3);
            }
        });

        table.add(skin1Button).padRight(20).height(80f).width(80f);
        table.add(skin2Button).padRight(20).height(80f).width(80f);
        table.add(skin3Button).height(80f).width(80f).row();

        table.add(skin1Label).padRight(40).padTop(10f);
        table.add(skin2Label).padRight(40).padTop(10f);
        table.add(skin3Label).padTop(20f).row();

        return table;
    }
}
