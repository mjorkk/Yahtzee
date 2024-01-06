package feri.rri.yahtzee.screen;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.ScreenAdapter;
        import com.badlogic.gdx.assets.AssetManager;
        import com.badlogic.gdx.graphics.g2d.TextureAtlas;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import com.badlogic.gdx.scenes.scene2d.Actor;
        import com.badlogic.gdx.scenes.scene2d.InputEvent;
        import com.badlogic.gdx.scenes.scene2d.Stage;
        import com.badlogic.gdx.scenes.scene2d.ui.Image;
        import com.badlogic.gdx.scenes.scene2d.ui.Label;

        import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
        import com.badlogic.gdx.scenes.scene2d.ui.Skin;
        import com.badlogic.gdx.scenes.scene2d.ui.Table;
        import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
        import com.badlogic.gdx.scenes.scene2d.ui.TextField;
        import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
        import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
        import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
        import com.badlogic.gdx.utils.Align;
        import com.badlogic.gdx.utils.ScreenUtils;
        import com.badlogic.gdx.utils.viewport.FitViewport;
        import com.badlogic.gdx.utils.viewport.Viewport;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

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

        stage.addActor(createUi());
        Drawable quitButtonDrawable = new TextureRegionDrawable(gameplayAtlas.findRegion(RegionNames.SCORES_LABEL));
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.background = quitButtonDrawable;
        Image scoresLabel = new Image(quitButtonDrawable);

        // Set the position of the image to be centered at the top of the stage
        scoresLabel.setSize(450f,70f);
        scoresLabel.setPosition(viewport.getWorldWidth()/ 2 - scoresLabel.getWidth() / 2, viewport.getWorldHeight() - scoresLabel.getHeight()-30f);

        stage.addActor(scoresLabel);
        Gdx.input.setInputProcessor(stage);
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

    public class Player implements Comparable<Player> {
        String name;
        int score;

        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(Player other) {
            return other.score - this.score; // Sort in descending order
        }
    }
    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(10);

        List<Player> players = new ArrayList<>();
        players.add(new Player("Pkgnrtihgiuerthgie oiteoirtjgietrg", 500));
        players.add(new Player("Player 2", 450));
        players.add(new Player("Player 3", 600));
        for (int i = 4; i <= 20; i++) {
            players.add(new Player("Player " + i, i * 50));
        }
        Collections.sort(players);

        // Add sorted players to the table
        for (Player player : players) {
            addScoreRow(table, player);
        }

        // Create a scrollable window for the leaderboard
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(true);
        scrollPane.setScrollingDisabled(true, false);

        // Add the scrollable window to the stage
        Table windowTable = new Table();
        windowTable.add(scrollPane).padTop(170f).padBottom(20f).width(800f);
        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.PLAIN_BACKGROUND);
        windowTable.setBackground(new TextureRegionDrawable(backgroundRegion));

        // Add a back button to return to the main menu
        TextButton backButton = new TextButton("Back", skin);
        backButton.getLabel().setAlignment(Align.center);
        backButton.getLabelCell().padRight(25f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        windowTable.row();
        windowTable.add(backButton).width(150f).padBottom(40f).height(70f);

        windowTable.setFillParent(true);
        windowTable.pack();

        return windowTable;
    }


    private int rankCounter = 1;

    private void addScoreRow(Table table, Player player) {
        Label rankLabel = new Label(String.valueOf(rankCounter++), skin);
        rankLabel.setAlignment(Align.right);
        TextField playerNameField = new TextField(player.name, skin);
        playerNameField.setDisabled(true);
        playerNameField.getStyle().background.setLeftWidth(20);
        playerNameField.setFocusTraversal(false);
        TextField playerScoreField = new TextField(String.valueOf(player.score), skin);
        playerScoreField.setDisabled(true);
        playerScoreField.getStyle().background.setLeftWidth(20);
        playerScoreField.setFocusTraversal(false);
        table.add(rankLabel);
        table.add(playerNameField).expandX().fillX().height(70f);
        table.add(playerScoreField).expandX().fillX().height(70f);
        table.row();
    }


}

