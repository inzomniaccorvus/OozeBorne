package arena.shooter.entities;

import arena.shooter.core.GameAssets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BasicEnemy extends Enemy {
    public BasicEnemy(float x, float y, float speed) {
        super(x, y, 20f, speed, 5, Color.RED, 10);
        collisionRadius = 48f;
        bodyRadius = 32f;
    }

    @Override
    protected Animation<TextureRegion> getAnimation(GameAssets assets) {
        return assets.basicAnim;
    }
}
