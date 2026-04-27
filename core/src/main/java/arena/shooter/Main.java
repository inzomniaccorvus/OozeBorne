package arena.shooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Vector3 mouseWorldPosition;

    private Sound shootSound;
    private Sound hitSound;
    private Sound explosionSound;
    private Sound pickupSound;

    private Player player;
    private BulletManager bulletManager;
    private EnemyManager enemyManager;
    private WaveManager waveManager;
    private ParticleSystem particleSystem;
    private DropManager dropManager;
    private CollisionSystem collisionSystem;
    private HUD hud;
    private Arena arena;

    private int score;
    private float survivalTime;

    private boolean mainMenu;
    private boolean gameOver;

    private float shakeDuration;
    private float shakeOffsetX;
    private float shakeOffsetY;
    private static final float SHAKE_INTENSITY = 5f;
    private static final float SHAKE_DURATION = 0.2f;

    private static final float PLAYER_START_X = 300f;
    private static final float PLAYER_START_Y = 250f;
    private static final float PLAYER_SIZE = 30f;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1.5f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mouseWorldPosition = new Vector3();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shootSound = Gdx.audio.newSound(Gdx.files.internal("laserShoot.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitHurt.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("powerUp.wav"));

        player = new Player(PLAYER_START_X, PLAYER_START_Y, PLAYER_SIZE);
        bulletManager = new BulletManager();
        enemyManager = new EnemyManager();
        waveManager = new WaveManager();
        particleSystem = new ParticleSystem();
        dropManager = new DropManager();
        collisionSystem = new CollisionSystem();
        arena = new Arena(0);
        hud = new HUD();

        mainMenu = true;
        gameOver = false;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (mainMenu) {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            spriteBatch.begin();
            hud.drawMainMenu(spriteBatch, font);
            spriteBatch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) mainMenu = false;
            return;
        }

        if (gameOver) {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            spriteBatch.begin();
            hud.drawGameOver(spriteBatch, font, score, survivalTime);
            spriteBatch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) restartGame();
            return;
        }

        if (waveManager.gameWon) {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            spriteBatch.begin();
            hud.drawGameWin(spriteBatch, font, score, survivalTime);
            spriteBatch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) restartGame();
            return;
        }

        if (waveManager.betweenWaves) {
            waveManager.tickIntro(delta);
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            spriteBatch.begin();
            hud.drawWaveIntro(spriteBatch, font, waveManager.currentWave);
            spriteBatch.end();
            return;
        }

        survivalTime += delta;

        mouseWorldPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorldPosition);
        float mouseX = mouseWorldPosition.x;
        float mouseY = mouseWorldPosition.y;

        float aimDeltaX = mouseX - player.centerX();
        float aimDeltaY = mouseY - player.centerY();
        float aimLength = (float) Math.sqrt(aimDeltaX * aimDeltaX + aimDeltaY * aimDeltaY);
        float aimDirectionX = aimDeltaX / aimLength;
        float aimDirectionY = aimDeltaY / aimLength;

        player.update(delta, bulletManager.bullets, aimDirectionX, aimDirectionY, shootSound);
        bulletManager.update(delta);
        enemyManager.update(waveManager, delta, player.centerX(), player.centerY());
        particleSystem.update(delta);
        dropManager.update(delta, player, pickupSound);

        score += collisionSystem.checkBulletEnemyCollisions(bulletManager, enemyManager, particleSystem, hitSound, explosionSound);
        collisionSystem.checkPlayerEnemyCollisions(player, enemyManager);
        collisionSystem.checkEnemyBulletPlayerCollisions(player, enemyManager);

        if (player.damageFlashTimer > 0) shakeDuration = SHAKE_DURATION;
        if (shakeDuration > 0) {
            shakeDuration -= delta;
            shakeOffsetX = (float) (Math.random() * SHAKE_INTENSITY * 2) - SHAKE_INTENSITY;
            shakeOffsetY = (float) (Math.random() * SHAKE_INTENSITY * 2) - SHAKE_INTENSITY;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        if (player.isDead()) {
            gameOver = true;
            return;
        }

        camera.position.set(Gdx.graphics.getWidth() / 2f + shakeOffsetX, Gdx.graphics.getHeight() / 2f + shakeOffsetY, 0);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hud.drawHPBar(shapeRenderer, player.hp);
        player.draw(shapeRenderer);
        bulletManager.draw(shapeRenderer);
        enemyManager.draw(shapeRenderer);
        dropManager.draw(shapeRenderer);
        particleSystem.drawShapes(shapeRenderer);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.line(player.centerX(), player.centerY(), mouseX, mouseY);
        shapeRenderer.end();

        spriteBatch.begin();
        particleSystem.drawText(spriteBatch, font);
        font.setColor(1f, 1f, 1f, 1f);
        hud.drawGameInfo(spriteBatch, font, player, score, survivalTime);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        shootSound.dispose();
        hitSound.dispose();
        explosionSound.dispose();
        pickupSound.dispose();
    }

    private void restartGame() {
        player.reset(PLAYER_START_X, PLAYER_START_Y);
        bulletManager.clear();
        enemyManager.clear();
        particleSystem.clear();
        waveManager.clear();
        dropManager.clear();
        score = 0;
        survivalTime = 0f;
        shakeDuration = 0f;
        shakeOffsetX = 0f;
        shakeOffsetY = 0f;
        gameOver = false;
        mainMenu = false;
    }
}
