package arena.shooter.screens;

import arena.shooter.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen extends ScreenAdapter {
    private final Main game;

    public MainMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        game.batch.begin();
        game.hud.drawMainMenu(game.batch, game.font);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }
}
