package arena.shooter.entities;

import arena.shooter.core.GameAssets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TankEnemy extends Enemy {
    public TankEnemy(float x, float y, float speed) {
        super(x, y, 35f, speed, 18, Color.PURPLE, 30);
        collisionRadius = 83f;
        bodyRadius = 60f;
    }
    @Override
    protected Animation<TextureRegion> getAnimation(GameAssets assets) {
        return assets.tankAnim;
    }
}

