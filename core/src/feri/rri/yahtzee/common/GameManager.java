package feri.rri.yahtzee.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

import feri.rri.yahtzee.CellState;
import feri.rri.yahtzee.Yahtzee;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String SOUND_PREF = "soundPref";
    private static final String MUSIC_PREF = "musicPref";

    private final Preferences PREFS;
    private Boolean soundOn = true;
    private Boolean musicOn = true;
    private Music backgroundMusic;

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

}
