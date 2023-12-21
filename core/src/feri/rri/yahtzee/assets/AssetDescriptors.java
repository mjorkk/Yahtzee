package feri.rri.yahtzee.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<BitmapFont> UI_FONT =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);

    public static final AssetDescriptor<Skin> UI_SKIN =
            new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY =
            new AssetDescriptor<TextureAtlas>(AssetPaths.GAMEPLAY, TextureAtlas.class);
    public static final AssetDescriptor<Music> MENU_MUSIC =
            new AssetDescriptor<Music>(AssetPaths.MENU_MUSIC, Music.class);
    public static final AssetDescriptor<Music> GAME_MUSIC =
            new AssetDescriptor<Music>(AssetPaths.GAME_MUSIC, Music.class);

    private AssetDescriptors() {
    }
}
