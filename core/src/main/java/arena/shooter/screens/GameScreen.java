
package arena.shooter.screens;

import arena.shooter.Main;
import arena.shooter.core.Constants;
import arena.shooter.core.GameAssets;
import arena.shooter.entities.*;
import arena.shooter.systems.*;
import arena.shooter.ui.HUD;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen extends ScreenAdapter {
    private final Main game;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private FitViewport viewport;
    private FitViewport hudViewport;
    private Vector3 mouseWorldPosition;

    private Player player;
    private BulletManager bulletManager;
    private EnemyManager enemyManager;
    private WaveManager waveManager;
    private ParticleSystem particleSystem;
    private DropManager dropManager;
    private CollisionSystem collisionSystem;
    private HUD hud;

    private int score;
    private float survivalTime;

    private float shakeDuration;
    private float shakeOffsetX;
    private float shakeOffsetY;

    private GameAssets assets;


    public GameScreen(Main game) {
        this.game = game;
        this.assets = game.assets;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, camera);

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, hudCamera);

        mouseWorldPosition = new Vector3();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        player = new Player(Constants.PLAYER_START_X, Constants.PLAYER_START_Y, Constants.PLAYER_SIZE);
        bulletManager = new BulletManager();
        enemyManager = new EnemyManager();
        waveManager = new WaveManager();
        particleSystem = new ParticleSystem();
        dropManager = new DropManager();
        collisionSystem = new CollisionSystem();
        hud = new HUD();

        score = 0;
        survivalTime = 0f;
        shakeDuration = 0f;
    }

    @Override
    public void render(float delta) {
        if (waveManager.gameWon) {
            game.scoreManager.addScore(score);
            game.setScreen(new GameWinScreen(game, score, survivalTime));
            return;
        }

        if (waveManager.betweenWaves) {
            waveManager.tickIntro(delta);
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            game.batch.setProjectionMatrix(hudCamera.combined);
            game.batch.begin();
            hud.drawWaveIntro(game.batch, game.font, waveManager.currentWave);
            game.batch.end();
            return;
        }

        survivalTime += delta;

        mouseWorldPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouseWorldPosition);
        float mouseX = mouseWorldPosition.x;
        float mouseY = mouseWorldPosition.y;

        float aimDeltaX = mouseX - player.centerX();
        float aimDeltaY = mouseY - player.centerY();
        float aimLength = (float) Math.sqrt(aimDeltaX * aimDeltaX + aimDeltaY * aimDeltaY);
        float aimDirectionX = aimDeltaX / aimLength;
        float aimDirectionY = aimDeltaY / aimLength;

        player.update(delta, bulletManager, aimDirectionX, aimDirectionY, game.shootSound);
        bulletManager.update(delta);
        enemyManager.update(waveManager, delta, player.centerX(), player.centerY());
        particleSystem.update(delta);
        dropManager.update(delta, player, game.pickupSound);

        score += collisionSystem.checkBulletEnemyCollisions(bulletManager, enemyManager, particleSystem, game.hitSound, game.explosionSound, player);
        collisionSystem.checkPlayerEnemyCollisions(player, enemyManager);
        collisionSystem.checkEnemyBulletPlayerCollisions(player, enemyManager);

        if (player.damageFlashTimer > 0) shakeDuration = Constants.SHAKE_DURATION;
        if (shakeDuration > 0) {
            shakeDuration -= delta;
            shakeOffsetX = (float) (Math.random() * Constants.SHAKE_INTENSITY * 2) - Constants.SHAKE_INTENSITY;
            shakeOffsetY = (float) (Math.random() * Constants.SHAKE_INTENSITY * 2) - Constants.SHAKE_INTENSITY;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        if (player.isDead()) {
            game.scoreManager.addScore(score);
            game.setScreen(new GameOverScreen(game, score, survivalTime));
            return;
        }

        camera.position.set(Constants.SCREEN_WIDTH / 2f + shakeOffsetX, Constants.SCREEN_HEIGHT / 2f + shakeOffsetY, 0);
        camera.update();
        hudCamera.position.set(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, 0);
        hudCamera.update();

        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(assets.getMapTexture(waveManager.currentWave),
            0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        player.draw(game.batch, assets);
        enemyManager.draw(game.batch, assets);
        bulletManager.draw(game.batch, assets);
        dropManager.draw(game.batch, assets);
        particleSystem.drawText(game.batch, game.font);

        game.batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        particleSystem.drawShapes(shapeRenderer);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        float crossSize = 20f;
        shapeRenderer.line(mouseX - crossSize, mouseY, mouseX + crossSize, mouseY);
        shapeRenderer.line(mouseX, mouseY - crossSize, mouseX, mouseY + crossSize);
        shapeRenderer.circle(mouseX, mouseY, 6f);
        shapeRenderer.line(player.centerX(), player.centerY(), mouseX, mouseY);
        shapeRenderer.end();

        hudViewport.apply();
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hud.drawHPBar(shapeRenderer, player.hp);
        shapeRenderer.end();

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();
        hud.drawGameInfo(game.batch, game.font, player, score, survivalTime);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        bulletManager.clear();
        enemyManager.clear();
        particleSystem.clear();
        waveManager.clear();
        dropManager.clear();
    }
}
