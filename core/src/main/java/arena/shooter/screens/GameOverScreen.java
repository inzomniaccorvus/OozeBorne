package arena.shooter.screens;

import arena.shooter.Main;
import arena.shooter.core.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen extends ScreenAdapter {
    private final Main game;
    private final int score;
    private final float survivalTime;
    private OrthographicCamera camera;
    private FitViewport viewport;

    public GameOverScreen(Main game, int score, float survivalTime) {
        this.game = game;
        this.score = score;
        this.survivalTime = survivalTime;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, camera);
        camera.position.set(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.04f, 0.04f, 0.08f, 1f);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.hud.drawGameOver(game.batch, game.fonts, score, survivalTime);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.R))
            game.setScreen(new GameScreen(game));
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }
}
