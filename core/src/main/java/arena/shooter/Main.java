package arena.shooter;

import arena.shooter.core.FontManager;
import arena.shooter.core.GameAssets;
import arena.shooter.screens.MainMenuScreen;
import arena.shooter.systems.ScoreManager;
import arena.shooter.ui.HUD;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;

    public Sound shootSound;
    public Sound hitSound;
    public Sound explosionSound;
    public Sound pickupSound;

    public Music menuMusic;
    public Music map1Music;
    public Music map2Music;
    public Music map3Music;
    public Music bossMusic;
    public Music currentMusic;

    public ScoreManager scoreManager;
    public HUD hud;
    public GameAssets assets;
    public FontManager fonts;

    @Override
    public void create() {
        batch = new SpriteBatch();

        shootSound     = Gdx.audio.newSound(Gdx.files.internal("fireball.mp3"));
        hitSound       = Gdx.audio.newSound(Gdx.files.internal("hitHurt.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        pickupSound    = Gdx.audio.newSound(Gdx.files.internal("pick_up.mp3"));

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_music.mp3"));
        map1Music = Gdx.audio.newMusic(Gdx.files.internal("map1_music.mp3"));
        map2Music = Gdx.audio.newMusic(Gdx.files.internal("map2_music.ogg"));
        map3Music = Gdx.audio.newMusic(Gdx.files.internal("map3_music.ogg"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("boss_music.mp3"));

        menuMusic.setLooping(true);
        map1Music.setLooping(true);
        map2Music.setLooping(true);
        map3Music.setLooping(true);
        bossMusic.setLooping(true);

        menuMusic.setVolume(0.8f);
        map1Music.setVolume(0.8f);
        map2Music.setVolume(0.8f);
        map3Music.setVolume(0.8f);
        bossMusic.setVolume(0.8f);

        scoreManager = new ScoreManager();
        scoreManager.load();

        assets = new GameAssets();
        assets.load();

        fonts = new FontManager();
        fonts.load();

        hud = new HUD();

        playMusic(menuMusic);
        setScreen(new MainMenuScreen(this));
    }

    public void playMusic(Music music) {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
        currentMusic = music;
        currentMusic.play();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shootSound.dispose();
        hitSound.dispose();
        explosionSound.dispose();
        pickupSound.dispose();
        menuMusic.dispose();
        map1Music.dispose();
        map2Music.dispose();
        map3Music.dispose();
        bossMusic.dispose();
        assets.dispose();
        fonts.dispose();
    }
}
