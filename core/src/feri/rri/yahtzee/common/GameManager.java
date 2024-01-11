package feri.rri.yahtzee.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

import feri.rri.yahtzee.Yahtzee;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String SOUND_PREF = "soundPref";
    private static final String MUSIC_PREF = "musicPref";
    private static final String SKIN_PREF = "skinPref";

    private final Preferences PREFS;
    private Boolean soundOn;
    private Boolean musicOn;
    private Music backgroundMusic;
    private Integer _skin;
    private static final String LUCK_TEST_PREF = "luckTestPref";
    private Boolean luckTestOn;

    public void setBackgroundMusic(Music backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
        this.backgroundMusic.setVolume(0.2f);
        this.backgroundMusic.setLooping(true);
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    private GameManager() {
        PREFS = Gdx.app.getPreferences(Yahtzee.class.getSimpleName());
        musicOn = PREFS.getBoolean(MUSIC_PREF,true);
        soundOn = PREFS.getBoolean(SOUND_PREF,true);
        _skin = PREFS.getInteger(SKIN_PREF,1);
        luckTestOn = PREFS.getBoolean(LUCK_TEST_PREF, false);
    }


    public Boolean getSoundPref() {
        return soundOn;
    }

    public void setSoundPref(Boolean state) {
        soundOn = state;
        PREFS.putBoolean(SOUND_PREF, state);
        PREFS.flush();
    }

    public Boolean getMusicPref() {
        return musicOn;
    }

    public void setMusicPref(Boolean state) {
        musicOn = state;
        PREFS.putBoolean(MUSIC_PREF, state);
        PREFS.flush();
    }

    public Integer getSkinPref(){return _skin;}

    public void setSkinPref(Integer skin) {
        _skin = skin;
        PREFS.putInteger(SKIN_PREF, skin);
        PREFS.flush();
    }
    public Boolean getLuckTestPref() {
        return luckTestOn;
    }

    public void setLuckTestPref(Boolean state) {
        luckTestOn = state;
        PREFS.putBoolean(LUCK_TEST_PREF, state);
        PREFS.flush();
    }

}
