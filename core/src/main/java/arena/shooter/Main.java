package arena.shooter;

import arena.shooter.core.GameAssets;
import arena.shooter.screens.MainMenuScreen;
import arena.shooter.systems.ScoreManager;
import arena.shooter.ui.HUD;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    public Sound shootSound;
    public Sound hitSound;
    public Sound explosionSound;
    public Sound pickupSound;
    public Music bgMusic;

    public ScoreManager scoreManager;
    public HUD hud;
    public GameAssets assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        shootSound = Gdx.audio.newSound(Gdx.files.internal("laserShoot.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitHurt.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("powerUp.wav"));
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bgmusic.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.4f);
        bgMusic.play();

        scoreManager = new ScoreManager();
        scoreManager.load();

        hud = new HUD();

        assets = new GameAssets();
        assets.load();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shootSound.dispose();
        hitSound.dispose();
        explosionSound.dispose();
        pickupSound.dispose();
        bgMusic.dispose();
        assets.dispose();
    }
}
