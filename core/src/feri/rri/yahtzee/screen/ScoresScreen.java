package feri.rri.yahtzee.screen;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.ScreenAdapter;
        import com.badlogic.gdx.assets.AssetManager;
        import com.badlogic.gdx.graphics.g2d.TextureAtlas;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import com.badlogic.gdx.scenes.scene2d.Actor;
        import com.badlogic.gdx.scenes.scene2d.InputEvent;
        import com.badlogic.gdx.scenes.scene2d.Stage;
        import com.badlogic.gdx.scenes.scene2d.ui.Label;
        import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
        import com.badlogic.gdx.scenes.scene2d.ui.Skin;
        import com.badlogic.gdx.scenes.scene2d.ui.Table;
        import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
        import com.badlogic.gdx.scenes.scene2d.ui.TextField;
        import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
        import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
        import com.badlogic.gdx.utils.ScreenUtils;
        import com.badlogic.gdx.utils.viewport.FitViewport;
        import com.badlogic.gdx.utils.viewport.Viewport;

        import feri.rri.yahtzee.Yahtzee;
        import feri.rri.yahtzee.assets.AssetDescriptors;
        import feri.rri.yahtzee.assets.RegionNames;
        import feri.rri.yahtzee.common.GameManager;
        import feri.rri.yahtzee.config.GameConfig;

public class ScoresScreen extends ScreenAdapter {

    private final Yahtzee game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas gameplayAtlas;

    public ScoresScreen(Yahtzee game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT));
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createUi());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
// ...

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        // Add your UI elements here
        Label titleLabel = new Label("Leaderboard", skin);
        table.add(titleLabel).colspan(2).padBottom(30).center();
        table.row();

        // Add hardcoded scores for demonstration
        addScoreRow(table, "Player 1", 500);
        addScoreRow(table, "Player 2", 450);
        addScoreRow(table, "Player 3", 600);
        // Add more demonstration text to extend the scrollable content
        for (int i = 4; i <= 20; i++) {
            addScoreRow(table, "Player " + i, i * 50);
        }

        // Create a scrollable window for the leaderboard
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);

        // Add the scrollable window to the stage
        Table windowTable = new Table();
        windowTable.add(scrollPane).pad(20).expand().fill();

        // Add a back button to return to the main menu
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        windowTable.row();
        windowTable.add(backButton).padTop(30).center();

        windowTable.setFillParent(true);
        windowTable.pack();

        return windowTable;
    }

// ...

    private void addScoreRow(Table table, String playerName, int score) {
        TextField nameTextField = new TextField(playerName, skin);
        TextField scoreTextField = new TextField(String.valueOf(score), skin);

        // Set properties to make the TextField non-editable and not focused
        nameTextField.setDisabled(true);
        nameTextField.setFocusTraversal(false);
        scoreTextField.setDisabled(true);
        scoreTextField.setFocusTraversal(false);

        table.add(nameTextField).padRight(20);
        table.add(scoreTextField).padRight(20);
        table.row();
    }

}

