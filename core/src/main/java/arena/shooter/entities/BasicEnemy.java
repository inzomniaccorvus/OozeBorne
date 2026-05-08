package arena.shooter.entities;

import arena.shooter.core.GameAssets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BasicEnemy extends Enemy {
    public BasicEnemy(float x, float y, float speed) {
        super(x, y, 20f, speed, 3, Color.RED, 10);
        collisionRadius = 55f;
    }

    @Override
    protected Animation<TextureRegion> getAnimation(GameAssets assets) {
        return assets.basicAnim;
    }
}
