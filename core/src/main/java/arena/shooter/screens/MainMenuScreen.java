package arena.shooter.screens;

import arena.shooter.Main;
import arena.shooter.core.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen extends ScreenAdapter {
    private final Main game;
    private Texture backgroundTexture;
    private OrthographicCamera camera;
    private FitViewport viewport;

    public MainMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("menu_bg.png"));
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
        game.batch.draw(backgroundTexture, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        game.hud.drawMainMenu(game.batch, game.fonts, game.scoreManager.getScores());
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override
    public void dispose() { backgroundTexture.dispose(); }
}
