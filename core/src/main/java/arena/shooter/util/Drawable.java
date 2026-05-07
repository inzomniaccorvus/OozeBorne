package arena.shooter.util;
import arena.shooter.core.GameAssets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Drawable {
    void draw(SpriteBatch batch, GameAssets assets);
}
