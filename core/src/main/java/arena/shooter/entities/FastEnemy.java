package arena.shooter.entities;

import arena.shooter.core.GameAssets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FastEnemy extends Enemy {
    public FastEnemy(float x, float y, float speed) {
        super(x, y, 12f, speed, 1, Color.YELLOW, 15);
    }

    @Override
    protected Animation<TextureRegion> getAnimation(GameAssets assets) {
        return assets.fastAnim;
    }

}
