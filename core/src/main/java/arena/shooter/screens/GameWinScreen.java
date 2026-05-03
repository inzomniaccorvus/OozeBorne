package arena.shooter.screens;

import arena.shooter.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameWinScreen extends ScreenAdapter {
    private final Main game;
    private final int score;
    private final float survivalTime;

    public GameWinScreen(Main game, int score, float survivalTime) {
        this.game = game;
        this.score = score;
        this.survivalTime = survivalTime;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        game.batch.begin();
        game.hud.drawGameWin(game.batch, game.font, score, survivalTime);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new GameScreen(game));
        }
    }
}
